package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDao
import com.example.data.AppDatabase
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
 * Regression test for the "content goes stale on day 2" bug: the sync/seed guards
 * counted digest items across all dates, so once 15+ rows existed from any previous
 * day the sync was skipped forever. The fix scopes the count to a single date via
 * getDigestItemsCountByDate().
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DigestDateScopingTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    private fun story(headline: String, date: String) = DigestItem(
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
        date = date
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
    fun `digest item counts are scoped by date`() = runBlocking {
        // Insert 18 items dated yesterday.
        dao.insertDigestItems((1..18).map { story("Yesterday story $it", "2026-09-06") })

        assertEquals(18, dao.getDigestItemsCount())
        assertEquals(0, dao.getDigestItemsCountByDate("2026-09-07"))

        // Insert 5 items dated today.
        dao.insertDigestItems((1..5).map { story("Today story $it", "2026-09-07") })

        assertEquals(5, dao.getDigestItemsCountByDate("2026-09-07"))
        assertEquals(23, dao.getDigestItemsCount())
    }
}
