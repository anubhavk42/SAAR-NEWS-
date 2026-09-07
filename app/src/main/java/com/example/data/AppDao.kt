package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Digest Items
    @Query("SELECT * FROM digest_items ORDER BY date DESC, id DESC")
    fun getAllDigestItems(): Flow<List<DigestItem>>

    @Query("SELECT * FROM digest_items WHERE date = :date")
    fun getDigestItemsByDate(date: String): Flow<List<DigestItem>>

    @Query("SELECT * FROM digest_items WHERE id = :id")
    fun getDigestItemById(id: Long): Flow<DigestItem?>

    @Query("SELECT * FROM digest_items WHERE id = :id")
    suspend fun getDigestItemByIdSuspend(id: Long): DigestItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDigestItems(items: List<DigestItem>)

    @Update
    suspend fun updateDigestItem(item: DigestItem)

    @Query("UPDATE digest_items SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean)

    @Query("UPDATE digest_items SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: Long, isRead: Boolean)

    @Query("SELECT * FROM digest_items WHERE isBookmarked = 1 ORDER BY date DESC, id DESC")
    fun getBookmarkedDigestItems(): Flow<List<DigestItem>>

    @Query("SELECT COUNT(*) FROM digest_items")
    suspend fun getDigestItemsCount(): Int

    @Query("SELECT COUNT(*) FROM digest_items WHERE date = :date")
    suspend fun getDigestItemsCountByDate(date: String): Int

    @Query("SELECT headline FROM digest_items WHERE date = :date")
    suspend fun getDigestHeadlinesByDate(date: String): List<String>

    // Up to [limit] earlier stories (history only, excluding [excludeDate]) sharing a category.
    @Query("SELECT * FROM digest_items WHERE category = :category AND date != :excludeDate ORDER BY date DESC, id DESC LIMIT :limit")
    suspend fun getRelatedStories(category: String, excludeDate: String, limit: Int = 3): List<DigestItem>

    @Query("DELETE FROM digest_items")
    suspend fun deleteAllDigestItems()

    // Editorial Items
    @Query("SELECT * FROM editorial_items ORDER BY date DESC, id DESC")
    fun getAllEditorialItems(): Flow<List<EditorialItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEditorialItems(items: List<EditorialItem>)

    @Query("SELECT * FROM editorial_items WHERE id = :id")
    fun getEditorialItemById(id: Long): Flow<EditorialItem?>

    // Quiz Questions
    @Query("SELECT * FROM quiz_questions WHERE date = :date")
    fun getQuizQuestionsByDate(date: String): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE date = :date")
    suspend fun getQuizQuestionsByDateSuspend(date: String): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions WHERE id IN (:ids)")
    suspend fun getQuizQuestionsByIds(ids: List<Long>): List<QuizQuestion>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuizQuestions(questions: List<QuizQuestion>)

    // Quiz Results
    @Query("SELECT * FROM quiz_results WHERE date = :date LIMIT 1")
    fun getQuizResultByDate(date: String): Flow<QuizResult?>

    @Query("SELECT * FROM quiz_results WHERE date = :date LIMIT 1")
    suspend fun getQuizResultByDateSuspend(date: String): QuizResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)

    @Query("DELETE FROM quiz_results WHERE date = :date")
    suspend fun deleteQuizResultByDate(date: String)

    // Translation Cache
    @Query("SELECT * FROM translation_cache WHERE originalTextHash = :hash AND language = :language LIMIT 1")
    suspend fun getTranslation(hash: Int, language: String): TranslationCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationCache)

    // Review Schedule (spaced repetition)
    @Upsert
    suspend fun upsertReviewScheduleItem(item: ReviewScheduleItem)

    @Query("SELECT * FROM review_schedule_items WHERE quizQuestionId = :quizQuestionId LIMIT 1")
    suspend fun getReviewScheduleItemByQuestionId(quizQuestionId: Long): ReviewScheduleItem?

    @Query("SELECT * FROM review_schedule_items WHERE dueDate <= :today ORDER BY dueDate ASC, id ASC")
    fun getDueReviewItems(today: String): Flow<List<ReviewScheduleItem>>

    @Query("SELECT * FROM review_schedule_items WHERE dueDate <= :today ORDER BY dueDate ASC, id ASC")
    suspend fun getDueReviewItemsSuspend(today: String): List<ReviewScheduleItem>

    @Query("DELETE FROM review_schedule_items WHERE id = :id")
    suspend fun clearReviewScheduleItem(id: Long)
}
