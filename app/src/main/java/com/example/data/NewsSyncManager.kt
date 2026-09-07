package com.example.data

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.example.BuildConfig
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

object NewsSyncManager {

    private const val TAG = "NewsSyncManager"

    private const val GEMINI_MODEL = "gemini-flash-latest"

    /** Hard client-side ceiling for the whole live sync so a cold start never blocks the UI on it. */
    private const val SYNC_TIMEOUT_MS = 15_000L

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        // Bound each individual NewsAPI/Gemini call so the withTimeoutOrNull boundary
        // below isn't defeated by a socket stuck in a blocking read.
        .callTimeout(SYNC_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .build()

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    /**
     * Cleans and extracts raw JSON from potential markdown wrappers.
     */
    private fun cleanJsonString(input: String): String {
        var cleaned = input.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substringAfter("```json")
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substringAfter("```")
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substringBeforeLast("```")
        }
        return cleaned.trim()
    }

    /**
     * Decides whether to replace the live-pipeline results with Gemini-generated
     * fictional news.
     *
     * Returns true ONLY when [liveItems] is empty. For an exam-prep app, a short list
     * of genuine articles is always more valuable than a full list of invented ones,
     * so we keep whatever real news came back — even a single item — and never pad it
     * out with generated content.
     */
    @VisibleForTesting
    internal fun shouldUseGeneratedFallback(liveItems: List<DigestItem>): Boolean {
        return liveItems.isEmpty()
    }

    /**
     * RSS is the primary live source. If it returned any articles we skip the secondary
     * NewsAPI call entirely to conserve request quota; only an empty RSS result falls
     * through to NewsAPI. This uses the same "is it empty" bar as
     * [shouldUseGeneratedFallback] — the threshold is not lowered further.
     */
    @VisibleForTesting
    internal fun shouldSkipNewsApi(rssArticles: List<RawArticle>): Boolean {
        return rssArticles.isNotEmpty()
    }

    /**
     * Drops RSS articles already stored as a [DigestItem] headline for today. Feeds
     * overlap day to day, so without this the same stories would be re-enriched every
     * sync. Exact (case-sensitive) title match; [existingTodayHeadlines] must be scoped
     * to today's date only — never compared against older history.
     */
    @VisibleForTesting
    internal fun dedupRssAgainstExisting(
        rssArticles: List<RawArticle>,
        existingTodayHeadlines: Collection<String>
    ): List<RawArticle> {
        if (existingTodayHeadlines.isEmpty()) return rssArticles
        val seen = existingTodayHeadlines.toHashSet()
        return rssArticles.filterNot { it.title in seen }
    }

    /**
     * Counters for one [syncDailyNews] run, populated as the pipeline progresses so a
     * single end-to-end summary line can be logged. Observability only — no decisions
     * are made from this.
     */
    @VisibleForTesting
    internal class SyncStats {
        var rssFetched: Int = 0
        var rssSurvivedRelevance: Int = 0
        var rssSurvivedDedup: Int = 0
        /** Which live tier produced the enriched items: "RSS", "NewsAPI", or "none". */
        var liveTier: String = "none"
    }

    /**
     * Runs the live content tiers in order — RSS (primary), then NewsAPI (secondary) —
     * and routes whichever produced articles through the shared [enrich] step. NewsAPI
     * is not invoked at all when RSS returns anything ([shouldSkipNewsApi]).
     *
     * The source operations are parameters so the tier ordering can be tested without
     * touching the network. [stats] is filled in for the end-of-sync summary log.
     */
    @VisibleForTesting
    internal suspend fun collectLiveDigestItems(
        dateStr: String,
        existingTodayHeadlines: Collection<String>,
        rssSource: suspend () -> List<RawArticle>,
        newsApiSource: suspend () -> List<RawArticle>,
        enrich: suspend (List<RawArticle>, String) -> List<DigestItem>,
        stats: SyncStats = SyncStats()
    ): List<DigestItem> {
        val rssArticles = rssSource()
        if (shouldSkipNewsApi(rssArticles)) {
            val fresh = dedupRssAgainstExisting(rssArticles, existingTodayHeadlines)
            stats.rssSurvivedDedup = fresh.size
            Log.d(TAG, "RSS: ${rssArticles.size} relevant, ${fresh.size} new after dedup; skipping NewsAPI. Sending to Gemini...")
            val enriched = enrich(fresh, dateStr)
            if (enriched.isNotEmpty()) stats.liveTier = "RSS"
            return enriched
        }

        Log.d(TAG, "RSS returned nothing usable; falling through to NewsAPI...")
        val newsApiArticles = newsApiSource()
        if (newsApiArticles.isEmpty()) return emptyList()
        Log.d(TAG, "Fetched ${newsApiArticles.size} NewsAPI articles. Sending to Gemini...")
        val enriched = enrich(newsApiArticles, dateStr)
        if (enriched.isNotEmpty()) stats.liveTier = "NewsAPI"
        return enriched
    }

    /**
     * Fetches the RSS feeds, applies the relevance filter, and adapts each surviving
     * [NewsRssFetcher.RssArticle] into the [RawArticle] shape the Gemini enrichment
     * step already consumes. RSS-only fields (link, pubDate) are dropped here —
     * enrichment does not use them.
     */
    private suspend fun fetchRssRawArticles(stats: SyncStats): List<RawArticle> {
        return try {
            val fetched = NewsRssFetcher.fetchAllFeeds()
            val relevant = fetched.filter { NewsRssFetcher.isRelevant(it) }
            stats.rssFetched = fetched.size
            stats.rssSurvivedRelevance = relevant.size
            Log.d(
                TAG,
                "RSS relevance filter: ${fetched.size} fetched, ${relevant.size} relevant " +
                    "(${fetched.size - relevant.size} dropped as personal-finance/lifestyle/entertainment noise)"
            )
            relevant.map { rss ->
                RawArticle(
                    title = rss.title,
                    description = rss.description.orEmpty(),
                    sourceName = rss.sourceName
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "RSS fetch failed; will fall through to NewsAPI", e)
            emptyList()
        }
    }

    /** NewsAPI fetch, gated on a real key being configured (empty list otherwise). */
    private suspend fun fetchNewsApiRawArticlesOrEmpty(): List<RawArticle> {
        val newsApiKey = BuildConfig.NEWS_API_KEY
        val hasNewsApiKey = newsApiKey.isNotEmpty() && newsApiKey != "MY_NEWS_API_KEY"
        return if (hasNewsApiKey) fetchRawNewsFromApi(newsApiKey) else emptyList()
    }

    /**
     * Synchronizes news items. It checks if a live News API key is available.
     * If so, it queries top headlines for the country and processes them through Gemini.
     * If not, it generates 15-20 highly realistic, everyday national news stories using Gemini.
     */
    suspend fun syncDailyNews(repository: AppRepository, forceRefresh: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        try {
            val today = getTodayDateString()
            
            // If not forcing refresh, check if we already have 15+ items for today.
            if (!forceRefresh) {
                val currentDigestCount = repository.getDigestItemsCountByDate(today)
                if (currentDigestCount >= 15) {
                    Log.d(TAG, "Already have $currentDigestCount items. Skipping sync.")
                    return@withContext true
                }
            }

            // Bound the entire live -> generated pipeline. If it can't produce content
            // within SYNC_TIMEOUT_MS (slow connection, hung request), abandon it and
            // report failure so the caller falls through to the seed-data tier instead
            // of leaving the user on the splash screen. The three-tier fallback order
            // (live -> generated -> seed) is unchanged; this only caps how long we wait.
            val syncSucceeded = withTimeoutOrNull(SYNC_TIMEOUT_MS) {
                val stats = SyncStats()

                // Live tiers, in order: RSS feeds (primary) -> NewsAPI /v2/everything
                // (secondary). Both feed the same Gemini enrichment step.
                var digestItems: List<DigestItem> = collectLiveDigestItems(
                    dateStr = today,
                    existingTodayHeadlines = repository.getDigestHeadlinesByDate(today),
                    rssSource = { fetchRssRawArticles(stats) },
                    newsApiSource = { fetchNewsApiRawArticlesOrEmpty() },
                    enrich = ::cleanAndEnrichWithGemini,
                    stats = stats
                )

                // Fallback: only when the live pipeline returned nothing at all. A short list
                // of genuine articles is always preferable to a full list of invented ones.
                val useGeneratedFallback = shouldUseGeneratedFallback(digestItems)
                Log.d(TAG, "Live pipeline returned ${digestItems.size} item(s). Generated fallback firing: $useGeneratedFallback")
                if (useGeneratedFallback) {
                    Log.d(TAG, "No live news key or empty feed. Triggering fallback daily news automation via Gemini...")
                    digestItems = generateFallbackDailyNews(today)
                }

                // One end-to-end summary of the fetch -> filter -> dedup -> enrich chain.
                // The content tier reuses the same emptiness/fallback signals decided above.
                val contentTier = when {
                    digestItems.isEmpty() -> "seed (live + generated both empty; caller falls back to seed data)"
                    useGeneratedFallback -> "Gemini-generated"
                    else -> stats.liveTier
                }
                Log.d(
                    TAG,
                    "Sync summary — RSS fetched: ${stats.rssFetched}, " +
                        "survived relevance filter: ${stats.rssSurvivedRelevance}, " +
                        "survived dedup: ${stats.rssSurvivedDedup}, " +
                        "enriched & saved: ${digestItems.size}, content tier: $contentTier"
                )

                if (digestItems.isNotEmpty()) {
                    Log.d(TAG, "Successfully prepared ${digestItems.size} news stories. Inserting to database...")
                    // Insert without clearing the table first. The unique index on
                    // (headline, date) plus @Insert(onConflict = IGNORE) means stories
                    // already present are skipped, preserving their bookmark and read
                    // state, while genuinely new stories are still added.
                    repository.insertDigestItems(digestItems)
                    true
                } else {
                    false
                }
            }

            if (syncSucceeded == null) {
                Log.w(TAG, "News sync exceeded ${SYNC_TIMEOUT_MS}ms; falling through to seed fallback.")
                return@withContext false
            }

            return@withContext syncSucceeded
        } catch (e: Exception) {
            Log.e(TAG, "Error in news sync: ", e)
            return@withContext false
        }
    }

    /**
     * Trusted Indian news domains for the NewsAPI /v2/everything query. NewsAPI's
     * top-headlines?country=in returns zero results on the free tier, so we scope
     * /v2/everything to these domains instead.
     */
    private val NEWS_API_DOMAINS = listOf(
        "thehindu.com",
        "indianexpress.com",
        "livemint.com",
        "ndtv.com"
    )

    /**
     * Builds the NewsAPI /v2/everything request URL, scoped to [NEWS_API_DOMAINS].
     * /v2/everything requires at least one of q, sources, or domains; a non-empty
     * domains list satisfies that (an empty list would 400).
     */
    @VisibleForTesting
    internal fun buildNewsApiUrl(apiKey: String): HttpUrl {
        require(NEWS_API_DOMAINS.isNotEmpty()) {
            "NewsAPI /v2/everything requires a non-empty domains list or it returns HTTP 400"
        }
        return HttpUrl.Builder()
            .scheme("https")
            .host("newsapi.org")
            .addPathSegments("v2/everything")
            .addQueryParameter("domains", NEWS_API_DOMAINS.joinToString(","))
            .addQueryParameter("sortBy", "publishedAt")
            .addQueryParameter("pageSize", "25")
            .addQueryParameter("language", "en")
            .addQueryParameter("apiKey", apiKey)
            .build()
    }

    /**
     * Fetches recent articles from trusted Indian news domains via NewsAPI /v2/everything.
     */
    private suspend fun fetchRawNewsFromApi(apiKey: String): List<RawArticle> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(buildNewsApiUrl(apiKey)).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "News API response failed: ${response.code}")
                    return@withContext emptyList()
                }
                val body = response.body?.string() ?: return@withContext emptyList()
                val json = JSONObject(body)
                val status = json.optString("status")
                if (status != "ok") {
                    Log.e(TAG, "News API returned non-ok status: $status")
                    return@withContext emptyList()
                }

                val articlesArray = json.optJSONArray("articles") ?: return@withContext emptyList()
                Log.d(TAG, "NewsAPI: fetched ${articlesArray.length()} articles, code=${response.code}")
                val list = mutableListOf<RawArticle>()
                for (i in 0 until articlesArray.length()) {
                    val art = articlesArray.getJSONObject(i)
                    val title = art.optString("title")
                    val description = art.optString("description", "")
                    val sourceName = art.optJSONObject("source")?.optString("name", "Unknown") ?: "Unknown"
                    if (title.isNotEmpty()) {
                        list.add(RawArticle(title = title, description = description, sourceName = sourceName))
                    }
                }
                return@withContext list
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching News API: ", e)
            return@withContext emptyList()
        }
    }

    /**
     * Uses Gemini to map raw articles, clean up description, simplify language, and enrich with perspectives.
     */
    private suspend fun cleanAndEnrichWithGemini(rawArticles: List<RawArticle>, dateStr: String): List<DigestItem> = withContext(Dispatchers.IO) {
        val geminiKey = BuildConfig.GEMINI_API_KEY
        if (geminiKey.isEmpty() || geminiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key missing. Cannot process news via Gemini.")
            return@withContext emptyList()
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent?key=$geminiKey"

        // Build a concise prompt with raw headlines
        val articlesJsonInput = JSONArray().apply {
            rawArticles.forEach { art ->
                put(JSONObject().apply {
                    put("title", art.title)
                    put("description", art.description)
                    put("source", art.sourceName)
                })
            }
        }

        val prompt = """
            You are a Senior Editor and News Simplification AI.
            I will give you a list of raw news articles fetched from a live News API.
            Process every article I give you, whatever the count. Do NOT invent, pad,
            or fabricate any extra articles to reach a target number such as 15.
            For each article, map and output a structured JSON object matching the following instructions:
            
            1. 'category': Must be one of: Polity, Economy, Environment, InternationalRelations, Science, Other.
            2. 'headline': A clear, direct headline (feel free to clean up trailing source names).
            3. 'previewText': A highly simplified, plain-language summary of EXACTLY 2 to 3 sentences. No complex jargon, easy for any everyday citizen to read under 30 seconds.
            4. 'contextText': 1-2 paragraphs of clear background info.
            5. 'keyPointsText': 3-4 clear bullet points of critical details.
            6. 'whyItMattersText': 1-2 sentences explaining the direct, real-world impact of this news on the common public's everyday life.
            7. 'examAngleText': Brief connection to general knowledge or public exams.
            8. 'sourceAFraming': Source framing 1 (e.g., 'The Hindu (Center-Left): ...')
            9. 'sourceBFraming': Source framing 2 (e.g., 'Indian Express (Liberal-Neutral): ...')
            10. 'sourceCFraming': Source framing 3 (e.g., 'LiveMint (Fiscal-Conservative): ...')
            11. 'isDailyPulse': Boolean. Set to true for everyday citizen-impact briefs (such as consumer guidelines, sports, local technology, national measures) and false for long form deep dives.
            
            Format the output strictly as a JSON array of objects. 
            Do NOT include any markdown blocks, prefix or postfix text, or any explanations. Return only the raw JSON.
            
            Raw articles:
            $articlesJsonInput
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder().url(url).post(requestBody).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val bodyString = response.body?.string() ?: return@withContext emptyList()
                val responseJson = JSONObject(bodyString)
                val text = responseJson.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: ""

                return@withContext parseJsonToDigestItems(text, dateStr)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning articles with Gemini: ", e)
            return@withContext emptyList()
        }
    }

    /**
     * Fallback dynamic content generator that prompts Gemini to synthesize 15-20 highly realistic, everyday national news briefs.
     */
    private suspend fun generateFallbackDailyNews(dateStr: String): List<DigestItem> = withContext(Dispatchers.IO) {
        val geminiKey = BuildConfig.GEMINI_API_KEY
        if (geminiKey.isEmpty() || geminiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key missing. Cannot generate fallback dynamic news.")
            return@withContext emptyList()
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent?key=$geminiKey"

        val prompt = """
            You are a Senior News Editor. Please generate exactly 15 to 20 highly realistic, real-world, everyday national news stories for India (country=in) for the date: $dateStr.
            
            These news briefs must feel concrete and directly impact the general public's daily life, cost of living, local technology, sports, national welfare, consumer business, and environment.
            
            Generate a variety of stories across these categories: Polity, Economy, Environment, InternationalRelations, Science, Other.
            
            For each news brief, output a JSON object with:
            - 'category': One of: Polity, Economy, Environment, InternationalRelations, Science, Other.
            - 'headline': A direct, highly realistic news headline (e.g. 'National Railways Upgrades Local Trains with Air-Conditioned General Coaches', 'Government Extends Direct Subsidies for Rooftop Solar Panels').
            - 'previewText': A plain-language summary of EXACTLY 2 to 3 sentences that anyone can read in 30 seconds.
            - 'contextText': 1-2 paragraphs of background info.
            - 'keyPointsText': 3-4 bullet points of critical details.
            - 'whyItMattersText': 1-2 sentences explaining the direct, real-world impact of this news on the common public's everyday life.
            - 'examAngleText': Brief connection to general knowledge or public exams.
            - 'sourceAFraming': Source framing 1 (e.g., 'The Hindu (Center-Left): ...')
            - 'sourceBFraming': Source framing 2 (e.g., 'Indian Express (Liberal-Neutral): ...')
            - 'sourceCFraming': Source framing 3 (e.g., 'LiveMint (Fiscal-Conservative): ...')
            - 'isDailyPulse': Boolean. Set to true for everyday citizen-impact briefs (make 12-15 of these true) and false for long form deep dives.
            
            Format the output strictly as a JSON array of objects. 
            Do NOT include any markdown blocks, code blocks, prefix or postfix text, or any explanations. Return only raw, valid JSON.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder().url(url).post(requestBody).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val bodyString = response.body?.string() ?: return@withContext emptyList()
                val responseJson = JSONObject(bodyString)
                val text = responseJson.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: ""

                return@withContext parseJsonToDigestItems(text, dateStr)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating fallback news via Gemini: ", e)
            return@withContext emptyList()
        }
    }

    @VisibleForTesting
    internal fun parseJsonToDigestItems(rawJson: String, dateStr: String): List<DigestItem> {
        val list = mutableListOf<DigestItem>()
        try {
            val cleaned = cleanJsonString(rawJson)
            val array = JSONArray(cleaned)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val catStr = obj.optString("category", "Other")
                val category = try {
                    Category.valueOf(catStr)
                } catch (e: Exception) {
                    Category.Other
                }

                list.add(
                    DigestItem(
                        category = category,
                        headline = obj.optString("headline", ""),
                        previewText = obj.optString("previewText", ""),
                        contextText = obj.optString("contextText", ""),
                        keyPointsText = obj.optString("keyPointsText", ""),
                        whyItMattersText = obj.optString("whyItMattersText", ""),
                        examAngleText = obj.optString("examAngleText", ""),
                        sourceAFraming = obj.optString("sourceAFraming", ""),
                        sourceBFraming = obj.optString("sourceBFraming", ""),
                        sourceCFraming = obj.optString("sourceCFraming", ""),
                        date = dateStr,
                        isBookmarked = false,
                        isRead = false,
                        isDailyPulse = obj.optBoolean("isDailyPulse", true)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON to digest items: ", e)
        }
        return list
    }

    @VisibleForTesting
    internal data class RawArticle(
        val title: String,
        val description: String,
        val sourceName: String
    )
}
