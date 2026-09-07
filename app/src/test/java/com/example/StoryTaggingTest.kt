package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDao
import com.example.data.AppDatabase
import com.example.data.Category
import com.example.data.DatabaseSeeder
import com.example.data.NewsSyncManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P2 batch 2 Feature 1 — story topic tagging.
 *
 * DigestItem already carries a `category` (the Category enum, stored as a String via
 * Converters). This verifies the field survives a Room round-trip for stories from
 * BOTH origins:
 *  - the static seeder (DatabaseSeeder assigns Category directly)
 *  - the RSS/Gemini pipeline (parseJsonToDigestItems reads the category the shared
 *    enrichment prompt assigns)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StoryTaggingTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.appDao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `seed-sourced story keeps its category through a Room round-trip`() = runBlocking {
        val seed = DatabaseSeeder.getSeedDigestItems()
        val economySeed = seed.first { it.category == Category.Economy }

        dao.insertDigestItems(listOf(economySeed))

        val stored = dao.getAllDigestItems().first().single { it.headline == economySeed.headline }
        assertEquals(Category.Economy, stored.category)
    }

    @Test
    fun `RSS-pipeline story is tagged by enrichment and keeps its category through Room`() = runBlocking {
        // Shape of a single enriched article as Gemini returns it for an RSS-derived story.
        val geminiJson = """
            [
              {
                "category": "Environment",
                "headline": "Coastal states finalise mangrove restoration targets",
                "previewText": "Four states agreed on a shared timeline.",
                "contextText": "c", "keyPointsText": "k", "whyItMattersText": "w",
                "examAngleText": "e",
                "sourceAFraming": "a", "sourceBFraming": "b", "sourceCFraming": "c",
                "isDailyPulse": false
              }
            ]
        """.trimIndent()

        val parsed = NewsSyncManager.parseJsonToDigestItems(geminiJson, "2026-09-07")
        assertEquals(1, parsed.size)
        assertEquals(Category.Environment, parsed.single().category)

        dao.insertDigestItems(parsed)

        val stored = dao.getAllDigestItems().first()
            .single { it.headline == "Coastal states finalise mangrove restoration targets" }
        assertEquals(Category.Environment, stored.category)
        assertTrue(stored.date == "2026-09-07")
    }
}
