package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDao
import com.example.data.AppDatabase
import com.example.data.QuizQuestion
import com.example.data.ReviewScheduleItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * P2 Feature 1 — review scheduling data layer. Same in-memory Room approach as
 * DigestBookmarkPersistenceTest. Verifies the "due for review" query (dueDate <= today)
 * and the clear-a-specific-item query.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReviewScheduleDataTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    private fun iso(daysFromNow: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysFromNow)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    private val today get() = iso(0)

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

    private fun quizQuestion(id: Long) = QuizQuestion(
        id = id,
        date = today,
        questionText = "Q$id",
        option1 = "a", option2 = "b", option3 = "c", option4 = "d",
        correctOptionIndex = 0
    )

    @Test
    fun `due query returns only items whose dueDate is today or earlier`() = runBlocking {
        // review_schedule_items.quizQuestionId is a FK to quiz_questions.id
        dao.insertQuizQuestions(listOf(quizQuestion(1), quizQuestion(2)))

        dao.upsertReviewScheduleItem(
            ReviewScheduleItem(quizQuestionId = 1, dueDate = today, intervalStage = 1, createdDate = today)
        )
        dao.upsertReviewScheduleItem(
            ReviewScheduleItem(quizQuestionId = 2, dueDate = iso(10), intervalStage = 1, createdDate = today)
        )

        val due = dao.getDueReviewItemsSuspend(today)

        assertEquals(1, due.size)
        assertEquals(1L, due.single().quizQuestionId)
    }

    @Test
    fun `clearing an item removes it from the due query results`() = runBlocking {
        dao.insertQuizQuestions(listOf(quizQuestion(1)))
        dao.upsertReviewScheduleItem(
            ReviewScheduleItem(quizQuestionId = 1, dueDate = today, intervalStage = 1, createdDate = today)
        )

        val before = dao.getDueReviewItemsSuspend(today)
        assertEquals(1, before.size)

        dao.clearReviewScheduleItem(before.single().id)

        assertTrue(dao.getDueReviewItemsSuspend(today).isEmpty())
    }
}
