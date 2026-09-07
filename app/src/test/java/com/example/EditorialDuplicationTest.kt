package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDao
import com.example.data.AppDatabase
import com.example.data.EditorialItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Regression test for fix #5: editorial items used an autoGenerate primary key, so
 * @Insert(onConflict = IGNORE) never ignored anything and the seed editorials piled
 * up as duplicates every day. The unique index on (title, date) now makes a repeat
 * insert of the same editorial a no-op.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EditorialDuplicationTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    private val today = "2026-09-07"

    private fun editorials() = listOf(
        EditorialItem(title = "Editorial A", takeawayText = "takeaway A", fullAnalysisText = "analysis A", date = today),
        EditorialItem(title = "Editorial B", takeawayText = "takeaway B", fullAnalysisText = "analysis B", date = today),
        EditorialItem(title = "Editorial C", takeawayText = "takeaway C", fullAnalysisText = "analysis C", date = today)
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
    fun `re-inserting the same editorials does not create duplicates`() = runBlocking {
        dao.insertEditorialItems(editorials())
        dao.insertEditorialItems(editorials())

        val rows = dao.getAllEditorialItems().first()
        assertEquals(3, rows.size)
    }
}
