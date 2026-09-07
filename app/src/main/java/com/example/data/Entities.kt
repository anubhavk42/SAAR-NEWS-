package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "digest_items",
    indices = [Index(value = ["headline", "date"], unique = true)]
)
data class DigestItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: Category,
    val headline: String,
    val previewText: String,
    val contextText: String,
    val keyPointsText: String,
    val whyItMattersText: String,
    val examAngleText: String,
    val sourceAFraming: String,
    val sourceBFraming: String,
    val sourceCFraming: String,
    val date: String, // format YYYY-MM-DD
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val isDailyPulse: Boolean = false
)

@Entity(
    tableName = "editorial_items",
    indices = [Index(value = ["title", "date"], unique = true)]
)
data class EditorialItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val takeawayText: String,
    val fullAnalysisText: String,
    val date: String // format YYYY-MM-DD
)

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // format YYYY-MM-DD
    val questionText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctOptionIndex: Int
)

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // format YYYY-MM-DD
    val score: Int,
    val totalQuestions: Int
)

/**
 * Spaced-repetition schedule for a quiz question the user got wrong (P2 roadmap).
 * One row per question — only questions answered incorrectly are ever scheduled.
 *
 * [intervalStage]: 0 = not yet scheduled, 1 = due in 3 days, 2 = due in 7 days,
 * 3 = due in 21 days (and re-scheduled at +21 days if still missed at stage 3).
 */
@Entity(
    tableName = "review_schedule_items",
    foreignKeys = [
        ForeignKey(
            entity = QuizQuestion::class,
            parentColumns = ["id"],
            childColumns = ["quizQuestionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["quizQuestionId"], unique = true),
        Index(value = ["dueDate"])
    ]
)
data class ReviewScheduleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizQuestionId: Long,
    val dueDate: String,     // format YYYY-MM-DD
    val intervalStage: Int,
    val createdDate: String  // format YYYY-MM-DD
)

@Entity(tableName = "translation_cache", primaryKeys = ["originalTextHash", "language"])
data class TranslationCache(
    val originalTextHash: Int,
    val language: String,
    val originalText: String,
    val translatedText: String
)
