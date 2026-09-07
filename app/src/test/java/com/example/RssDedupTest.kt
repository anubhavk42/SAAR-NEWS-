package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.Category
import com.example.data.DigestItem
import com.example.data.NewsSyncManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Feature 3: RSS articles are de-duplicated against existing digest items for TODAY
 * before enrichment. Same in-memory-Room approach as DigestBookmarkPersistenceTest
 * (fix #1). Exact title match; today-scoped (a same-titled item from a prior day is
 * NOT treated as a duplicate).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RssDedupTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository

    private val today = "2026-09-07"
    private val yesterday = "2026-09-06"

    private fun digest(headline: String, date: String) = DigestItem(
        category = Category.Other,
        headline = headline,
        previewText = "", contextText = "", keyPointsText = "",
        whyItMattersText = "", examAngleText = "",
        sourceAFraming = "", sourceBFraming = "", sourceCFraming = "",
        date = date
    )

    private fun rss(title: String) = NewsSyncManager.RawArticle(title, "desc", "The Hindu")

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AppRepository(db.appDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `only articles whose title is not already in todays digest are forwarded`() = runBlocking {
        // Today's digest already contains "Story A" and "Story B".
        repository.insertDigestItems(listOf(digest("Story A", today), digest("Story B", today)))
        // "Story C" exists too, but only for YESTERDAY — it must NOT be filtered.
        repository.insertDigestItems(listOf(digest("Story C", yesterday)))

        val incoming = listOf(
            rss("Story A"), // dup of today -> dropped
            rss("Story B"), // dup of today -> dropped
            rss("Story C"), // only in yesterday -> kept
            rss("Story D"), // new -> kept
            rss("Story E")  // new -> kept
        )

        val existingTodayHeadlines = repository.getDigestHeadlinesByDate(today)
        assertEquals(setOf("Story A", "Story B"), existingTodayHeadlines.toSet())

        val forwarded = NewsSyncManager.dedupRssAgainstExisting(incoming, existingTodayHeadlines)

        assertEquals(3, forwarded.size)
        assertEquals(listOf("Story C", "Story D", "Story E"), forwarded.map { it.title })
    }
}
