package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.QuizQuestion
import com.example.data.ReviewScheduler
import com.example.utils.DateFormatter
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P2 Feature 2 — spaced-repetition scheduling on quiz submission. Robolectric +
 * in-memory Room (fix #1 / RssDedupTest pattern); exercises ReviewScheduler against
 * a real DAO.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReviewSchedulingLogicTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AppRepository(db.appDao)
        runBlocking {
            // review_schedule_items.quizQuestionId is a FK to quiz_questions.id
            repository.insertQuizQuestions((1L..4L).map {
                QuizQuestion(
                    id = it, date = DateFormatter.today(),
                    questionText = "Q$it",
                    option1 = "a", option2 = "b", option3 = "c", option4 = "d",
                    correctOptionIndex = 0
                )
            })
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `first-time wrong answer creates a stage-1 item due in 3 days`() = runBlocking {
        ReviewScheduler.onQuizAnswered(repository, quizQuestionId = 1, wasCorrect = false)

        val item = repository.getReviewScheduleItemByQuestionId(1)!!
        assertEquals(1, item.intervalStage)
        assertEquals(DateFormatter.daysFromToday(3), item.dueDate)
    }

    @Test
    fun `stage-1 item answered wrong again becomes stage-2 due in 7 days`() = runBlocking {
        ReviewScheduler.onQuizAnswered(repository, 1, wasCorrect = false)
        ReviewScheduler.onQuizAnswered(repository, 1, wasCorrect = false)

        val item = repository.getReviewScheduleItemByQuestionId(1)!!
        assertEquals(2, item.intervalStage)
        assertEquals(DateFormatter.daysFromToday(7), item.dueDate)
    }

    @Test
    fun `stage-2 item answered wrong again becomes stage-3 due in 21 days`() = runBlocking {
        repeat(3) { ReviewScheduler.onQuizAnswered(repository, 1, wasCorrect = false) }

        val item = repository.getReviewScheduleItemByQuestionId(1)!!
        assertEquals(3, item.intervalStage)
        assertEquals(DateFormatter.daysFromToday(21), item.dueDate)
    }

    @Test
    fun `stage-3 item wrong again stays stage-3 and re-schedules at 21 days`() = runBlocking {
        repeat(5) { ReviewScheduler.onQuizAnswered(repository, 1, wasCorrect = false) }

        val item = repository.getReviewScheduleItemByQuestionId(1)!!
        assertEquals(3, item.intervalStage)
        assertEquals(DateFormatter.daysFromToday(21), item.dueDate)
    }

    @Test
    fun `a due item answered correctly is removed from the schedule`() = runBlocking {
        ReviewScheduler.onQuizAnswered(repository, 1, wasCorrect = false)
        assertEquals(1, repository.getReviewScheduleItemByQuestionId(1)?.intervalStage)

        ReviewScheduler.onQuizAnswered(repository, 1, wasCorrect = true)

        assertNull(repository.getReviewScheduleItemByQuestionId(1))
    }

    @Test
    fun `a question answered correctly on first attempt never creates a schedule item`() = runBlocking {
        ReviewScheduler.onQuizAnswered(repository, 2, wasCorrect = true)

        assertNull(repository.getReviewScheduleItemByQuestionId(2))
    }
}
