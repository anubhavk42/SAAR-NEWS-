package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AppDao
import com.example.data.Category
import com.example.data.DigestItem
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
 * Regression test for the "sync destroys bookmarks" bug: a re-sync used to wipe the
 * digest_items table before re-inserting, losing every bookmark and read flag.
 *
 * With the unique index on (headline, date) plus @Insert(onConflict = IGNORE), an
 * overlapping re-insert now skips stories already present (keeping their state) and
 * only adds genuinely new ones.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DigestBookmarkPersistenceTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    private val today = "2026-09-07"

    private fun story(headline: String) = DigestItem(
        category = Category.Polity,
        headline = headline,
        previewText = "preview",
        contextText = "context",
        keyPointsText = "key points",
        whyItMattersText = "why it matters",
        examAngleText = "exam angle",
        sourceAFraming = "A",
        sourceBFraming = "B",
        sourceCFraming = "C",
        date = today
    )

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
    fun `overlapping re-sync preserves bookmark and read state and does not duplicate`() = runBlocking {
        // Insert 3 digest items for today.
        dao.insertDigestItems(
            listOf(story("Story A"), story("Story B"), story("Story C"))
        )

        val initial = dao.getAllDigestItems().first()
        assertEquals(3, initial.size)

        // Mark one bookmarked and another read.
        val storyA = initial.first { it.headline == "Story A" }
        val storyB = initial.first { it.headline == "Story B" }
        dao.updateBookmarkStatus(storyA.id, true)
        dao.updateReadStatus(storyB.id, true)

        // Re-sync with an overlapping list: the same 3 stories plus 2 new ones.
        dao.insertDigestItems(
            listOf(
                story("Story A"),
                story("Story B"),
                story("Story C"),
                story("Story D"),
                story("Story E")
            )
        )

        val after = dao.getAllDigestItems().first()

        // 5 rows, not 8 - the 3 overlapping stories were skipped, not duplicated.
        assertEquals(5, after.size)

        // The bookmarked story is still bookmarked.
        assertTrue(after.first { it.headline == "Story A" }.isBookmarked)

        // The read story is still marked read.
        assertTrue(after.first { it.headline == "Story B" }.isRead)
    }
}
