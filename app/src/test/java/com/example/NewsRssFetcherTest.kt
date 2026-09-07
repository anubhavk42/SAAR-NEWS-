package com.example

import com.example.data.NewsRssFetcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * v2 content pipeline slice 1 — RSS parsing.
 *
 * Robolectric is used only because NewsRssFetcher.parseRss() calls
 * android.util.Xml.newPullParser(), which needs the Android runtime. The tests
 * themselves feed hand-written XML strings straight to the parser — no network,
 * and no MockWebServer (it is not a project dependency and was not added).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NewsRssFetcherTest {

    @Test
    fun `parses full RSS 2_0 items with description and pubDate`() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
              <channel>
                <title>The Hindu - National</title>
                <link>https://www.thehindu.com/news/national/</link>
                <description>National news from The Hindu</description>
                <item>
                  <title>Parliament passes new bill</title>
                  <link>https://www.thehindu.com/news/national/article1</link>
                  <description>The bill was passed after a long debate.</description>
                  <pubDate>Mon, 08 Sep 2025 10:30:00 +0530</pubDate>
                  <category>National</category>
                </item>
                <item>
                  <title>Monsoon update for the week</title>
                  <link>https://www.thehindu.com/news/national/article2</link>
                  <description>Heavy rain is expected across the region.</description>
                  <pubDate>Mon, 08 Sep 2025 09:00:00 +0530</pubDate>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val articles = NewsRssFetcher.parseRss(xml, "The Hindu")

        assertEquals(2, articles.size)

        val first = articles[0]
        assertEquals("Parliament passes new bill", first.title)
        assertEquals("https://www.thehindu.com/news/national/article1", first.link)
        assertEquals("The bill was passed after a long debate.", first.description)
        assertEquals("Mon, 08 Sep 2025 10:30:00 +0530", first.pubDate)
        assertEquals("The Hindu", first.sourceName)

        val second = articles[1]
        assertEquals("Monsoon update for the week", second.title)
        assertEquals("Mon, 08 Sep 2025 09:00:00 +0530", second.pubDate)
        assertEquals("The Hindu", second.sourceName)
    }

    @Test
    fun `parses minimal PIB items with only title and link without crashing`() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
              <channel>
                <title>Press Information Bureau</title>
                <link>https://pib.gov.in</link>
                <item>
                  <title>Cabinet approves new infrastructure project</title>
                  <link>https://pib.gov.in/PressReleasePage.aspx?PRID=1234567</link>
                </item>
                <item>
                  <title>New welfare scheme launched for farmers</title>
                  <link>https://pib.gov.in/PressReleasePage.aspx?PRID=1234568</link>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val articles = NewsRssFetcher.parseRss(xml, "PIB")

        assertEquals(2, articles.size)

        val first = articles[0]
        assertEquals("Cabinet approves new infrastructure project", first.title)
        assertEquals("https://pib.gov.in/PressReleasePage.aspx?PRID=1234567", first.link)
        assertTrue("PIB items have no <description> — expected null/empty", first.description.isNullOrEmpty())
        assertFalse("missing <pubDate> should be defaulted to fetch time", first.pubDate.isNullOrEmpty())
        assertEquals("PIB", first.sourceName)

        assertEquals("New welfare scheme launched for farmers", articles[1].title)
        assertEquals("PIB", articles[1].sourceName)
    }

    @Test
    fun `sourceName is attached per feed`() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
              <channel>
                <item>
                  <title>Markets close higher</title>
                  <link>https://www.livemint.com/market/article</link>
                  <description>Benchmark indices ended the day in the green.</description>
                  <pubDate>Mon, 08 Sep 2025 08:00:00 +0530</pubDate>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        assertEquals("LiveMint", NewsRssFetcher.parseRss(xml, "LiveMint").single().sourceName)
        assertEquals("Indian Express", NewsRssFetcher.parseRss(xml, "Indian Express").single().sourceName)
        assertEquals("The Hindu", NewsRssFetcher.parseRss(xml, "The Hindu").single().sourceName)
    }
}
