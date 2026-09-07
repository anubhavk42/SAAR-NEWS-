package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.example.data.DigestItem
import com.example.data.QuizQuestion
import com.example.ui.MainViewModel

/**
 * P2 Feature 3 — "Review" tab: today's due spaced-repetition questions, answered
 * through the same quiz UI ([QuizActiveContent]). Answers route through the
 * ViewModel's [MainViewModel.selectReviewAnswer], which calls the feature-2 scheduler.
 */
@Composable
fun ReviewScreen(
    viewModel: MainViewModel,
    onNavigateToSummary: (Long) -> Unit = {}
) {
    val questions by viewModel.reviewQuestions.collectAsState()
    val loaded by viewModel.reviewSessionLoaded.collectAsState()
    val currentIndex by viewModel.reviewCurrentIndex.collectAsState()
    val answers by viewModel.reviewAnswers.collectAsState()
    val digestItems by viewModel.digestItemsFlow.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadReviewSession() }

    ReviewScreenContent(
        questions = questions,
        loaded = loaded,
        currentIndex = currentIndex,
        answers = answers,
        digestItems = digestItems,
        onOptionSelected = viewModel::selectReviewAnswer,
        onNext = viewModel::reviewNext,
        onPrevious = viewModel::reviewPrevious,
        onNavigateToSummary = onNavigateToSummary
    )
}

@Composable
fun ReviewScreenContent(
    questions: List<QuizQuestion>,
    loaded: Boolean,
    currentIndex: Int,
    answers: Map<Int, Int>,
    digestItems: List<DigestItem>,
    onOptionSelected: (questionIndex: Int, optionIndex: Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onNavigateToSummary: (Long) -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "SPACED REPETITION",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Review",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when {
                !loaded -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                questions.isEmpty() -> ReviewEmptyState()

                else -> {
                    val idx = currentIndex.coerceIn(0, questions.size - 1)
                    QuizActiveContent(
                        question = questions[idx],
                        currentIndex = idx,
                        totalQuestions = questions.size,
                        selectedOption = answers[idx],
                        digestItems = digestItems,
                        onOptionSelected = { optionIndex -> onOptionSelected(idx, optionIndex) },
                        onNext = onNext,
                        onPrevious = onPrevious,
                        onNavigateToSummary = onNavigateToSummary
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("review_empty_state"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nothing to review today",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Questions you miss in the daily quiz reappear here on a spaced schedule.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
