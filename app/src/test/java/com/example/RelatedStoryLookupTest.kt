package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.Category
import com.example.data.DigestItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P2 batch 2 Feature 2 — related-story lookup. Robolectric + in-memory Room, same
 * pattern as prior data tests. Verifies category match, today-exclusion, recency
 * ordering, and the limit.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RelatedStoryLookupTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository

    private val today = "2026-09-07"

    private fun story(headline: String, date: String, category: Category) = DigestItem(
        category = category,
        headline = headline,
        previewText = "", contextText = "", keyPointsText = "",
        whyItMattersText = "", examAngleText = "",
        sourceAFraming = "", sourceBFraming = "", sourceCFraming = "",
        date = date
    )

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
    fun `returns only earlier same-category stories, most recent first`() = runBlocking {
        repository.insertDigestItems(
            listOf(
                story("Aug economy 1", "2026-08-01", Category.Economy),
                story("Aug economy 2", "2026-08-15", Category.Economy),
                story("Sep economy", "2026-09-01", Category.Economy),
                story("Today economy", today, Category.Economy),
                story("Today polity", today, Category.Polity)
            )
        )

        val related = repository.getRelatedStories("Economy", excludeDate = today)

        assertEquals(
            listOf("Sep economy", "Aug economy 2", "Aug economy 1"),
            related.map { it.headline }
        )
    }

    @Test
    fun `limit parameter is respected`() = runBlocking {
        repository.insertDigestItems(
            listOf(
                story("Aug economy 1", "2026-08-01", Category.Economy),
                story("Aug economy 2", "2026-08-15", Category.Economy),
                story("Aug economy 3", "2026-08-20", Category.Economy),
                story("Aug economy 4", "2026-08-25", Category.Economy),
                story("Today economy", today, Category.Economy)
            )
        )

        val related = repository.getRelatedStories("Economy", excludeDate = today, limit = 3)

        assertEquals(3, related.size)
        assertEquals(
            listOf("Aug economy 4", "Aug economy 3", "Aug economy 2"),
            related.map { it.headline }
        )
    }
}
