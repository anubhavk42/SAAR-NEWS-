package com.example.data

import android.util.Log
import android.util.Xml
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * v2 content pipeline — slice 1: fetch and parse RSS from a fixed set of trusted
 * Indian news sources.
 *
 * This is a standalone, testable unit. It is intentionally NOT wired into
 * [NewsSyncManager] yet, and it does no deduplication, category filtering, date
 * parsing, or database writes — those are later roadmap tasks.
 */
object NewsRssFetcher {

    private const val TAG = "NewsRssFetcher"

    /** Per-feed client-side ceiling, mirroring the sync timeout from the NewsAPI path. */
    private const val FEED_TIMEOUT_MS = 15_000L

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .callTimeout(FEED_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .build()

    /** An RSS feed URL paired with the display name stamped onto its articles. */
    private data class Feed(val sourceName: String, val url: String)

    /** Source name that is exempt from relevance filtering — see [isRelevant]. */
    const val SOURCE_PIB = "PIB"

    private val FEEDS = listOf(
        Feed("The Hindu", "https://www.thehindu.com/news/national/?service=rss"),
        Feed("LiveMint", "https://www.livemint.com/rss/news"),
        Feed(SOURCE_PIB, "https://pib.gov.in/RssMain.aspx?ModId=6&Reg=3&Lang=1"),
        Feed("Indian Express", "https://indianexpress.com/section/india/feed")
    )

    /**
     * Personal-finance / lifestyle / entertainment terms that mark an RSS article as
     * noise for an exam-focused current-affairs digest. Exclude-only by design: a
     * current-affairs feed's valid topics (polity, economy, environment, IR, science,
     * health policy, …) are too wide to safely allowlist. All entries are lowercase;
     * matching is case-insensitive against title + description.
     */
    @VisibleForTesting
    internal val EXCLUDE_KEYWORDS = listOf(
        // personal finance / markets how-tos
        "mutual fund",
        "personal finance",
        "stock market tips",
        "stocks to buy",
        "credit card offer",
        "ipo listing",
        "tax saving tips",
        "best sip",
        "gold rate today",
        // entertainment
        "box office",
        "movie review",
        "film review",
        "web series",
        "trailer launch",
        "ott release",
        "celebrity",
        // lifestyle
        "recipe",
        "horoscope",
        "zodiac",
        "tarot",
        "fashion trends"
    )

    /**
     * Keyword relevance gate for a single RSS article.
     *
     * Returns false when the article's title or description contains any
     * [EXCLUDE_KEYWORDS] term (case-insensitive); true otherwise.
     *
     * PIB articles are government-curated source content by definition, so they are
     * always considered relevant regardless of keyword match.
     */
    @VisibleForTesting
    internal fun isRelevant(article: RssArticle): Boolean {
        if (article.sourceName == SOURCE_PIB) return true
        val haystack = (article.title + " " + article.description.orEmpty()).lowercase(Locale.ROOT)
        return EXCLUDE_KEYWORDS.none { haystack.contains(it) }
    }

    /**
     * One parsed RSS `<item>`.
     *
     * [description] and [pubDate] are nullable because the feeds are not uniform:
     * PIB items carry only a title and link. For an item with no `<pubDate>`, the
     * value is defaulted to the fetch time (PIB releases are same-day); a missing
     * `<description>` stays null (filled in later by Gemini enrichment).
     * [pubDate], when present, is the raw RSS date string — not parsed or reformatted.
     */
    data class RssArticle(
        val title: String,
        val link: String,
        val description: String?,
        val pubDate: String?,
        val sourceName: String
    )

    /**
     * Fetches all four feeds in parallel, parses each, and returns the combined list.
     *
     * A feed that fails — network error, non-2xx, timeout, malformed XML — is logged
     * and skipped; one broken feed never fails the whole fetch.
     */
    suspend fun fetchAllFeeds(): List<RssArticle> = coroutineScope {
        FEEDS
            .map { feed -> async(Dispatchers.IO) { fetchFeed(feed) } }
            .awaitAll()
            .flatten()
    }

    private suspend fun fetchFeed(feed: Feed): List<RssArticle> {
        return try {
            val xml = withTimeoutOrNull(FEED_TIMEOUT_MS) {
                val request = Request.Builder().url(feed.url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Feed ${feed.sourceName} failed: HTTP ${response.code}")
                        null
                    } else {
                        response.body?.string()
                    }
                }
            }

            if (xml.isNullOrBlank()) {
                Log.w(TAG, "Feed ${feed.sourceName}: no content (timeout, error, or empty body); skipping")
                emptyList()
            } else {
                parseRss(xml, feed.sourceName).also {
                    Log.d(TAG, "Feed ${feed.sourceName}: parsed ${it.size} articles")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Feed ${feed.sourceName}: error; skipping", e)
            emptyList()
        }
    }

    /**
     * Parses RSS 2.0 `<item>` elements into [RssArticle]s.
     *
     * Tolerant of missing fields: an item needs only a non-empty `<title>` and
     * `<link>` to be kept. A missing `<description>` yields null; a missing
     * `<pubDate>` is defaulted to the current fetch time.
     */
    @VisibleForTesting
    internal fun parseRss(xml: String, sourceName: String): List<RssArticle> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml.trim()))

        val articles = mutableListOf<RssArticle>()

        var inItem = false
        var title: String? = null
        var link: String? = null
        var description: String? = null
        var pubDate: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> when (parser.name.lowercase(Locale.ROOT)) {
                    "item" -> {
                        inItem = true
                        title = null; link = null; description = null; pubDate = null
                    }
                    "title" -> if (inItem) title = readText(parser)
                    "link" -> if (inItem) link = readText(parser)
                    "description" -> if (inItem) description = readText(parser)
                    "pubdate" -> if (inItem) pubDate = readText(parser)
                }

                XmlPullParser.END_TAG -> if (parser.name.equals("item", ignoreCase = true)) {
                    val t = title
                    val l = link
                    if (!t.isNullOrEmpty() && !l.isNullOrEmpty()) {
                        articles.add(
                            RssArticle(
                                title = t,
                                link = l,
                                description = description?.takeIf { it.isNotBlank() },
                                pubDate = pubDate?.takeIf { it.isNotBlank() } ?: nowRssDate(),
                                sourceName = sourceName
                            )
                        )
                    }
                    inItem = false
                }
            }
            eventType = parser.next()
        }
        return articles
    }

    private fun readText(parser: XmlPullParser): String {
        return try {
            parser.nextText().trim()
        } catch (e: Exception) {
            ""
        }
    }

    private fun nowRssDate(): String =
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US).format(Date())
}
