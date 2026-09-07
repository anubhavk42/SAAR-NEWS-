package com.example.data

import androidx.annotation.VisibleForTesting
import com.example.utils.DateFormatter

/**
 * Spaced-repetition scheduling for quiz mistakes (P2 roadmap).
 *
 * Fixed-interval schedule, all offsets measured from today:
 *   stage 1 -> due in 3 days, stage 2 -> 7 days, stage 3 -> 21 days.
 * A question missed again while at stage 3 is re-scheduled at +21 days but stays
 * at stage 3 — it neither escalates further nor leaves rotation.
 *
 * Only mistakes are ever scheduled: a question answered correctly on the first
 * attempt (no existing schedule) never enters the review system.
 */
object ReviewScheduler {

    private const val STAGE_1_DAYS = 3
    private const val STAGE_2_DAYS = 7
    private const val STAGE_3_DAYS = 21

    /** Due-in-days offset for a target interval stage. Stage 3 and beyond stay at 21. */
    @VisibleForTesting
    internal fun daysForStage(stage: Int): Int = when {
        stage <= 1 -> STAGE_1_DAYS
        stage == 2 -> STAGE_2_DAYS
        else -> STAGE_3_DAYS
    }

    /**
     * Applies the spaced-repetition rules after a single quiz question is graded.
     *
     * - correct + no existing schedule -> no-op (first-attempt success never enters review)
     * - correct + existing schedule    -> remove it (learned; exits rotation)
     * - wrong + no existing schedule    -> create at stage 1, due in 3 days
     * - wrong + existing at stage N     -> advance to stage min(N + 1, 3) and re-due from today
     */
    suspend fun onQuizAnswered(
        repository: AppRepository,
        quizQuestionId: Long,
        wasCorrect: Boolean
    ) {
        val existing = repository.getReviewScheduleItemByQuestionId(quizQuestionId)

        if (wasCorrect) {
            if (existing != null) repository.clearReviewScheduleItem(existing.id)
            return
        }

        val nextStage = when {
            existing == null -> 1
            existing.intervalStage >= 3 -> 3
            else -> existing.intervalStage + 1
        }

        repository.upsertReviewScheduleItem(
            ReviewScheduleItem(
                id = existing?.id ?: 0,
                quizQuestionId = quizQuestionId,
                dueDate = DateFormatter.daysFromToday(daysForStage(nextStage)),
                intervalStage = nextStage,
                createdDate = existing?.createdDate ?: DateFormatter.today()
            )
        )
    }
}
