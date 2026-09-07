package com.example

import com.example.data.NewsSyncManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Fix #8: NewsAPI top-headlines?country=in returns zero results on the free tier.
 * The fetch now uses /v2/everything scoped to a fixed set of trusted Indian domains.
 * This verifies the URL builder produces that request (network is not exercised).
 */
class NewsApiUrlTest {

    @Test
    fun `url uses the everything endpoint scoped to the four indian domains`() {
        val url = NewsSyncManager.buildNewsApiUrl("TEST_KEY")
        val asString = url.toString()

        // /v2/everything, not /v2/top-headlines
        assertEquals(listOf("v2", "everything"), url.pathSegments)
        assertTrue("expected /v2/everything in $asString", asString.contains("/v2/everything"))
        assertFalse("must not use /v2/top-headlines", asString.contains("top-headlines"))

        // no country-code filter
        assertFalse("must not contain country=in", asString.contains("country=in"))
        assertEquals(null, url.queryParameter("country"))

        // all four expected domains present
        val domains = url.queryParameter("domains").orEmpty()
        for (domain in listOf("thehindu.com", "indianexpress.com", "livemint.com", "ndtv.com")) {
            assertTrue("expected domain '$domain' in '$domains'", domains.contains(domain))
        }
    }
}
