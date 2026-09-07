package com.example

import com.example.data.NewsRssFetcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Feature 4: keyword relevance filter for RSS articles.
 *
 * Plain JUnit — isRelevant() is a pure string function with no Android dependency.
 * Exclude-only: an EXCLUDE_KEYWORDS hit in the title or description drops the article,
 * except for PIB (government-curated, always kept).
 */
class RelevanceFilterTest {

    private fun article(
        title: String,
        description: String? = null,
        sourceName: String = "LiveMint"
    ) = NewsRssFetcher.RssArticle(
        title = title,
        link = "https://example.com/a",
        description = description,
        pubDate = "Mon, 08 Sep 2025 10:00:00 +0530",
        sourceName = sourceName
    )

    @Test
    fun `article with excluded keyword in the title is filtered out`() {
        assertFalse(
            NewsRssFetcher.isRelevant(article(title = "Best mutual fund picks for this year"))
        )
    }

    @Test
    fun `article with excluded keyword in the description is filtered out`() {
        assertFalse(
            NewsRssFetcher.isRelevant(
                article(
                    title = "Weekend entertainment roundup",
                    description = "The film had a strong box office opening across metros."
                )
            )
        )
    }

    @Test
    fun `genuine current-affairs article passes through`() {
        assertTrue(
            NewsRssFetcher.isRelevant(
                article(
                    title = "Supreme Court rules on electoral bonds scheme",
                    description = "A five-judge Constitution bench delivered the verdict on Thursday."
                )
            )
        )
    }

    @Test
    fun `PIB article is never filtered even with an excluded keyword`() {
        assertTrue(
            NewsRssFetcher.isRelevant(
                article(
                    title = "Government clarifies mutual fund taxation norms",
                    description = "The finance ministry issued a box office style press note.",
                    sourceName = NewsRssFetcher.SOURCE_PIB
                )
            )
        )
    }
}
