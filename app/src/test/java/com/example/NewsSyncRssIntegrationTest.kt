package com.example

import com.example.data.Category
import com.example.data.DigestItem
import com.example.data.NewsSyncManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Feature 2: RSS is wired in as the primary live source, ahead of NewsAPI.
 *
 * Fake source lambdas — the same "test the decision, no mock framework" approach as
 * NewsSyncFallbackTest. collectLiveDigestItems() takes its source operations as
 * parameters, so the tier ordering is verified without any network.
 *
 * Runs under Robolectric only so android.util.Log calls inside NewsSyncManager are
 * stubbed; the test itself exercises no Android APIs.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NewsSyncRssIntegrationTest {

    private fun digestFrom(raw: NewsSyncManager.RawArticle, date: String) = DigestItem(
        category = Category.Other,
        headline = raw.title,
        previewText = raw.description,
        contextText = "",
        keyPointsText = "",
        whyItMattersText = "",
        examAngleText = "",
        sourceAFraming = "",
        sourceBFraming = "",
        sourceCFraming = "",
        date = date
    )

    @Test
    fun `non-empty RSS result means the NewsAPI fetch is never called`() = runTest {
        var newsApiCalls = 0

        val items = NewsSyncManager.collectLiveDigestItems(
            dateStr = "2026-09-07",
            existingTodayHeadlines = emptySet(),
            rssSource = {
                listOf(
                    NewsSyncManager.RawArticle("RSS headline 1", "d1", "The Hindu"),
                    NewsSyncManager.RawArticle("RSS headline 2", "d2", "LiveMint")
                )
            },
            newsApiSource = { newsApiCalls++; emptyList() },
            enrich = { arts, date -> arts.map { digestFrom(it, date) } }
        )

        assertEquals("NewsAPI source must not be invoked when RSS has articles", 0, newsApiCalls)
        assertEquals(2, items.size)
        assertEquals("RSS headline 1", items[0].headline)
    }

    @Test
    fun `empty RSS result falls through to the NewsAPI fetch`() = runTest {
        var newsApiCalls = 0

        val items = NewsSyncManager.collectLiveDigestItems(
            dateStr = "2026-09-07",
            existingTodayHeadlines = emptySet(),
            rssSource = { emptyList() },
            newsApiSource = {
                newsApiCalls++
                listOf(NewsSyncManager.RawArticle("NewsAPI headline", "d", "NDTV"))
            },
            enrich = { arts, date -> arts.map { digestFrom(it, date) } }
        )

        assertEquals(1, newsApiCalls)
        assertEquals(1, items.size)
        assertEquals("NewsAPI headline", items[0].headline)
    }
}
