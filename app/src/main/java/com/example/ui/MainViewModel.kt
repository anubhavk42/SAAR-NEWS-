package com.example.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.utils.PdfGenerator
import com.example.work.NotificationScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database.appDao)
    private val preferencesManager = PreferencesManager(application)

    // Seeding state
    val isSeedingActive = MutableStateFlow(true)
    val isRefreshing = MutableStateFlow(false)

    // Settings States from DataStore
    val onboardingSeen = preferencesManager.onboardingSeenFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // null = DataStore has not been read yet. Distinguishing "unknown" from "false" keeps
    // navigation from locking onto the onboarding route on a cold start before the real
    // persisted value arrives.
    val onboardingSeenOrNull: StateFlow<Boolean?> = preferencesManager.onboardingSeenFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notificationPermissionRequested = preferencesManager.notificationPermissionRequestedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val darkMode = preferencesManager.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val themeProfile = preferencesManager.themeProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Light")

    val notificationTime = preferencesManager.notificationTimeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "08:00")

    val dataSaver = preferencesManager.dataSaverFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val topicPreferences = preferencesManager.topicPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val selectedLanguage = preferencesManager.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "English")

    // UI Category filters
    val homeCategoryFilter = MutableStateFlow<Category?>(null)
    val bookmarkCategoryFilter = MutableStateFlow<Category?>(null)

    // Current date today (used to match database dates)
    val todayDateString = DatabaseSeeder.getTodayDateString()

    // Room Database Observables
    val digestItemsFlow = repository.getAllDigestItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedItemsFlow = repository.getBookmarkedDigestItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val editorialItemsFlow = repository.getAllEditorialItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered lists
    val filteredHomeItems = combine(
        digestItemsFlow,
        homeCategoryFilter,
        topicPreferences
    ) { items, activeFilter, preferredTopics ->
        // 1. Filter by user's general topic preferences (if configured in settings, non-empty)
        val afterPreference = if (preferredTopics.isNotEmpty()) {
            items.filter { preferredTopics.contains(it.category.name) }
        } else {
            items
        }
        // 2. Filter by currently selected Category chip in Home
        if (activeFilter != null) {
            afterPreference.filter { it.category == activeFilter }
        } else {
            afterPreference
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredBookmarkedItems = combine(
        bookmarkedItemsFlow,
        bookmarkCategoryFilter
    ) { items, activeFilter ->
        if (activeFilter != null) {
            items.filter { it.category == activeFilter }
        } else {
            items
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Quiz States
    val quizQuestionsFlow = repository.getQuizQuestionsByDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizResultFlow = repository.getQuizResultByDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentQuizQuestionIndex = MutableStateFlow(0)
    val selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // index -> selectedOption (0..3)

    // Review (spaced repetition) states — a session is snapshotted on entry so answering
    // one question does not reshuffle the list mid-review.
    private val _reviewQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val reviewQuestions: StateFlow<List<QuizQuestion>> = _reviewQuestions.asStateFlow()

    private val _reviewSessionLoaded = MutableStateFlow(false)
    val reviewSessionLoaded: StateFlow<Boolean> = _reviewSessionLoaded.asStateFlow()

    val reviewCurrentIndex = MutableStateFlow(0)
    val reviewAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // index -> selectedOption (0..3)

    // Custom PDF selected IDs list
    val selectedForPdfIds = MutableStateFlow<Set<Long>>(emptySet())

    init {
        seedDatabaseIfNeeded()
    }

    private fun seedDatabaseIfNeeded() {
        viewModelScope.launch {
            try {
                isSeedingActive.value = true

                if (repository.getDigestItemsCountByDate(todayDateString) > 0) {
                    // Today's digest already has content (seed, partial, or full from an
                    // earlier run). Don't gate the UI on a fresh sync — unblock now and let
                    // one run in the background; new items land in the list via the Room Flow.
                    isSeedingActive.value = false
                    launch { runCatching { NewsSyncManager.syncDailyNews(repository, forceRefresh = false) } }
                } else {
                    // Nothing to show yet. Run the timeout-bounded live sync, then apply the
                    // seed-data tier if it still came up short. The UI unblocks as soon as
                    // this returns (bounded by SYNC_TIMEOUT_MS), not on the "best" tier.
                    val success = NewsSyncManager.syncDailyNews(repository, forceRefresh = false)
                    if (!success) {
                        val currentDigestCount = repository.getDigestItemsCountByDate(todayDateString)
                        if (currentDigestCount < 15) {
                            repository.insertDigestItems(DatabaseSeeder.getSeedDigestItems())
                        }
                    }
                }

                // Also check if quiz questions are empty
                val existingItems = repository.getQuizQuestionsByDateSuspend(todayDateString)
                if (existingItems.isEmpty()) {
                    repository.insertEditorialItems(DatabaseSeeder.getSeedEditorialItems())
                    repository.insertQuizQuestions(DatabaseSeeder.getSeedQuizQuestions())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Ultimate local fallback to ensure a robust user experience
                try {
                    val currentDigestCount = repository.getDigestItemsCountByDate(todayDateString)
                    if (currentDigestCount < 15) {
                        repository.insertDigestItems(DatabaseSeeder.getSeedDigestItems())
                    }
                } catch (innerEx: Exception) {
                    innerEx.printStackTrace()
                }
            } finally {
                isSeedingActive.value = false
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            try {
                isRefreshing.value = true
                NewsSyncManager.syncDailyNews(repository, forceRefresh = true)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getApplication(), "Failed to refresh news feed.", Toast.LENGTH_SHORT).show()
            } finally {
                isRefreshing.value = false
            }
        }
    }

    // Toggle Bookmarks & Reads
    fun toggleBookmark(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateBookmarkStatus(id, !currentStatus)
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            repository.updateReadStatus(id, true)
        }
    }

    // PDF Selection Toggle
    fun togglePdfSelection(id: Long) {
        val currentSet = selectedForPdfIds.value
        if (currentSet.contains(id)) {
            selectedForPdfIds.value = currentSet - id
        } else {
            selectedForPdfIds.value = currentSet + id
        }
    }

    // PDF Generation & Native Sharing
    fun generateAndSharePdf() {
        viewModelScope.launch {
            val itemsToPdf = if (selectedForPdfIds.value.isNotEmpty()) {
                digestItemsFlow.value.filter { selectedForPdfIds.value.contains(it.id) }
            } else {
                digestItemsFlow.value // Default to all today's items if none specifically selected
            }

            if (itemsToPdf.isEmpty()) {
                Toast.makeText(getApplication(), "No articles available to generate PDF.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val pdfFile = PdfGenerator.generateDigestPdf(
                context = getApplication(),
                dateString = todayDateString,
                items = itemsToPdf
            )

            if (pdfFile != null && pdfFile.exists()) {
                shareFile(pdfFile)
            } else {
                Toast.makeText(getApplication(), "Failed to generate PDF.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Generate and share the Premium Weekly Digest Dossier
    fun generateAndShareWeeklyPdf() {
        viewModelScope.launch {
            // Find most-read (isRead) or bookmarked items. Fall back to all if empty.
            var itemsToPdf = digestItemsFlow.value.filter { it.isRead || it.isBookmarked }
            if (itemsToPdf.isEmpty()) {
                // Take up to 7 most recent items as fallback
                itemsToPdf = digestItemsFlow.value.take(7)
            }

            if (itemsToPdf.isEmpty()) {
                Toast.makeText(getApplication(), "No analytical summaries available for Weekly Digest.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val pdfFile = PdfGenerator.generateWeeklyDigestPdf(
                context = getApplication(),
                items = itemsToPdf
            )

            if (pdfFile != null && pdfFile.exists()) {
                shareFile(pdfFile)
            } else {
                Toast.makeText(getApplication(), "Failed to compile Weekly Digest.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareFile(file: File) {
        val context = getApplication<Application>()
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Daily News SAAR - $todayDateString")
                putExtra(Intent.EXTRA_TEXT, "Here is your offline daily calm news SAAR summary.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share Daily SAAR PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Sharing failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    // Settings actions
    fun setOnboardingSeen(seen: Boolean) {
        viewModelScope.launch {
            preferencesManager.setOnboardingSeen(seen)
        }
    }

    fun setNotificationPermissionRequested(requested: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationPermissionRequested(requested)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun setThemeProfile(profile: String) {
        viewModelScope.launch {
            preferencesManager.setThemeProfile(profile)
        }
    }

    fun setNotificationTime(time: String) {
        viewModelScope.launch {
            preferencesManager.setNotificationTime(time)
            NotificationScheduler.scheduleDailyNotification(getApplication(), time)
        }
    }

    fun setDataSaver(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDataSaver(enabled)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    fun translateText(text: String, language: String, onCompleted: (String) -> Unit) {
        if (language == "English" || language.isEmpty()) {
            onCompleted(text)
            return
        }

        viewModelScope.launch {
            // 1. Check local DB translation cache first
            val cached = repository.getTranslation(text, language)
            if (cached != null) {
                onCompleted(cached)
                return@launch
            }

            // 2. Fallback to local static dictionary to keep it quick and save API quota
            val staticTranslation = TranslationHelper.translate(text, language)
            if (staticTranslation != text) {
                repository.saveTranslation(text, language, staticTranslation)
                onCompleted(staticTranslation)
                return@launch
            }

            // 3. Run through translation layer API (Gemini-3.5-flash)
            val apiTranslation = com.example.utils.GeminiTranslationClient.translateText(text, language)
            if (apiTranslation != text) {
                repository.saveTranslation(text, language, apiTranslation)
            }
            onCompleted(apiTranslation)
        }
    }

    fun toggleTopicPreference(topic: String) {
        viewModelScope.launch {
            val currentSet = topicPreferences.value
            val newSet = if (currentSet.contains(topic)) {
                currentSet - topic
            } else {
                currentSet + topic
            }
            preferencesManager.setTopicPreferences(newSet)
        }
    }

    // Quiz logic
    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        val currentAnswers = selectedAnswers.value.toMutableMap()
        currentAnswers[questionIndex] = optionIndex
        selectedAnswers.value = currentAnswers
    }

    fun submitQuiz() {
        viewModelScope.launch {
            val questions = quizQuestionsFlow.value
            val answers = selectedAnswers.value
            if (questions.isEmpty()) return@launch

            var score = 0
            for (i in questions.indices) {
                val selected = answers[i]
                if (selected == null) continue // skipped — neither a pass nor a mistake
                val wasCorrect = selected == questions[i].correctOptionIndex
                if (wasCorrect) score++
                // Route every graded answer through spaced-repetition scheduling.
                ReviewScheduler.onQuizAnswered(repository, questions[i].id, wasCorrect)
            }

            val result = QuizResult(
                date = todayDateString,
                score = score,
                totalQuestions = questions.size
            )
            repository.insertQuizResult(result)
        }
    }

    fun resetQuiz() {
        currentQuizQuestionIndex.value = 0
        selectedAnswers.value = emptyMap()
        viewModelScope.launch {
            repository.deleteQuizResultByDate(todayDateString)
        }
    }

    /** Up to 3 earlier stories in the same [category] (history only, excluding [currentDate]). */
    suspend fun getRelatedStories(category: String, currentDate: String): List<DigestItem> =
        repository.getRelatedStories(category, currentDate)

    // Review (spaced repetition) logic
    fun loadReviewSession() {
        viewModelScope.launch {
            val due = repository.getDueReviewItemsSuspend(todayDateString)
            val ids = due.map { it.quizQuestionId }
            _reviewQuestions.value = if (ids.isEmpty()) emptyList() else repository.getQuizQuestionsByIds(ids)
            reviewCurrentIndex.value = 0
            reviewAnswers.value = emptyMap()
            _reviewSessionLoaded.value = true
        }
    }

    fun selectReviewAnswer(questionIndex: Int, optionIndex: Int) {
        val question = _reviewQuestions.value.getOrNull(questionIndex) ?: return
        reviewAnswers.value = reviewAnswers.value + (questionIndex to optionIndex)
        viewModelScope.launch {
            // Same scheduling path as the main quiz.
            ReviewScheduler.onQuizAnswered(
                repository,
                question.id,
                wasCorrect = optionIndex == question.correctOptionIndex
            )
        }
    }

    fun reviewNext() {
        val idx = reviewCurrentIndex.value
        if (idx < _reviewQuestions.value.size - 1) reviewCurrentIndex.value = idx + 1
    }

    fun reviewPrevious() {
        val idx = reviewCurrentIndex.value
        if (idx > 0) reviewCurrentIndex.value = idx - 1
    }
}
