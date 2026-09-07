package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DigestItem
import com.example.data.QuizQuestion
import com.example.data.QuizResult
import com.example.ui.MainViewModel

@Composable
fun QuizScreen(
    viewModel: MainViewModel,
    onNavigateToSummary: (Long) -> Unit = {}
) {
    val questions by viewModel.quizQuestionsFlow.collectAsState()
    val savedResult by viewModel.quizResultFlow.collectAsState()
    val currentIndex by viewModel.currentQuizQuestionIndex.collectAsState()
    val answers by viewModel.selectedAnswers.collectAsState()
    val digestItems by viewModel.digestItemsFlow.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                if (savedResult == null && questions.isNotEmpty()) {
                    // Streamlined Quiz Header (Minimalist Progress Architecture)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "QUESTION ${currentIndex + 1} OF ${questions.size}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val percentComplete = (((currentIndex + 1).toFloat() / questions.size) * 100).toInt()
                            Text(
                                text = "$percentComplete% Complete",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    }
                } else {
                    // Header Label on Results / Loading States
                    Text(
                        text = "DAILY ASSESSMENT",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Title
                    Text(
                        text = if (savedResult != null) "Daily Evaluation" else "Daily Quiz",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                if (savedResult != null) {
                    // Quiz already completed today -> Show Saved Result
                    QuizResultsContent(
                        result = savedResult!!,
                        questions = questions,
                        answers = answers,
                        onRetake = {
                            viewModel.resetQuiz()
                        }
                    )
                } else if (questions.isEmpty()) {
                    // Loading or Empty State
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    // Quiz Active State with smooth horizontal slide transition
                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut()
                                )
                            } else {
                                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width } + fadeOut()
                                )
                            }
                        },
                        label = "quiz_question_slide",
                        modifier = Modifier.weight(1f)
                    ) { targetIndex ->
                        val question = questions[targetIndex]
                        val selectedOption = answers[targetIndex]

                        QuizActiveContent(
                            question = question,
                            currentIndex = targetIndex,
                            totalQuestions = questions.size,
                            selectedOption = selectedOption,
                            digestItems = digestItems,
                            onOptionSelected = { optionIndex ->
                                viewModel.selectQuizAnswer(targetIndex, optionIndex)
                            },
                            onNext = {
                                if (targetIndex < questions.size - 1) {
                                    viewModel.currentQuizQuestionIndex.value = targetIndex + 1
                                } else {
                                    viewModel.submitQuiz()
                                }
                            },
                            onPrevious = {
                                if (targetIndex > 0) {
                                    viewModel.currentQuizQuestionIndex.value = targetIndex - 1
                                }
                            },
                            onNavigateToSummary = onNavigateToSummary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizActiveContent(
    question: QuizQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    digestItems: List<DigestItem>,
    onOptionSelected: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onNavigateToSummary: (Long) -> Unit
) {
    val scrollState = rememberScrollState()
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Elegant Question Card with custom serif/sans-serif typography balance
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Options List
            val options = listOf(question.option1, question.option2, question.option3, question.option4)
            options.forEachIndexed { index, optionText ->
                val isSelected = selectedOption == index
                val hasSelected = selectedOption != null
                val isCorrectOption = index == question.correctOptionIndex

                // Determine precise premium colors to eliminate neon tones
                val (containerClr, borderClr, borderStrokeWidth, contentClr, indicatorBg, indicatorTextClr) = when {
                    !hasSelected -> {
                        // Regular state prior to answering
                        OptionColors(
                            containerClr = MaterialTheme.colorScheme.surface,
                            borderClr = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            borderStrokeWidth = 1.dp,
                            contentClr = MaterialTheme.colorScheme.onSurface,
                            indicatorBg = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            indicatorTextClr = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    isCorrectOption -> {
                        // Soft forest green accent for correct choices
                        OptionColors(
                            containerClr = if (isDark) Color(0xFF142416) else Color(0xFFEAF5EC),
                            borderClr = if (isDark) Color(0xFF2E6B34) else Color(0xFF388E3C),
                            borderStrokeWidth = 2.dp,
                            contentClr = if (isDark) Color(0xFFC8E6C9) else Color(0xFF1B5E20),
                            indicatorBg = if (isDark) Color(0xFF2E6B34) else Color(0xFFC8E6C9),
                            indicatorTextClr = if (isDark) Color(0xFFE8F5E9) else Color(0xFF1B5E20)
                        )
                    }
                    isSelected -> {
                        // Gentle, low-contrast earthy red/amber for the incorrect selected choice
                        OptionColors(
                            containerClr = if (isDark) Color(0xFF2A1614) else Color(0xFFFDF0ED),
                            borderClr = if (isDark) Color(0xFF8C2E29) else Color(0xFFC62828),
                            borderStrokeWidth = 2.dp,
                            contentClr = if (isDark) Color(0xFFFFCDD2) else Color(0xFFB71C1C),
                            indicatorBg = if (isDark) Color(0xFF8C2E29) else Color(0xFFFFCDD2),
                            indicatorTextClr = if (isDark) Color(0xFFFFEBEE) else Color(0xFFB71C1C)
                        )
                    }
                    else -> {
                        // Low-profile greyed out options that were not chosen
                        OptionColors(
                            containerClr = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            borderClr = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            borderStrokeWidth = 1.dp,
                            contentClr = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            indicatorBg = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                            indicatorTextClr = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !hasSelected) { onOptionSelected(index) }
                        .testTag("quiz_option_$index"),
                    colors = CardDefaults.cardColors(containerColor = containerClr),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(borderStrokeWidth, borderClr)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val letter = when (index) {
                            0 -> "A"
                            1 -> "B"
                            2 -> "C"
                            else -> "D"
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(color = indicatorBg, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                color = indicatorTextClr,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected || (hasSelected && isCorrectOption)) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = contentClr
                        )
                    }
                }
            }

            // Interactive Editorial Explanation
            AnimatedVisibility(
                visible = selectedOption != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val explanation = when {
                    question.questionText.contains("Municipal", ignoreCase = true) -> 
                        "Under the proposed Municipal Decentralization Bill, financial autonomy is granted to municipalities exceeding 1 Million in population, allowing them to issue municipal bonds and secure self-sustaining revenue structures."
                    question.questionText.contains("Repo Rate", ignoreCase = true) || question.questionText.contains("Monetary policy", ignoreCase = true) -> 
                        "The central bank paused the benchmark Repo Rate at 6.25% citing softening commodity prices and retail inflation declining to 4.1%, providing structural supply-side relief."
                    question.questionText.contains("Ramsar", ignoreCase = true) || question.questionText.contains("wetland", ignoreCase = true) -> 
                        "The Ramsar Convention (signed in 1971 in Ramsar, Iran) is the premier international intergovernmental treaty for the conservation and sustainable utilization of wetlands."
                    question.questionText.contains("quantum", ignoreCase = true) || question.questionText.contains("Kelvin", ignoreCase = true) -> 
                        "Physicists achieved stable 256-qubit quantum coherence at 0.5 Kelvin, demonstrating that diamond nitrogen-vacancy grids can maintain stability at slightly warmer sub-Kelvin states without expensive helium cooling."
                    question.questionText.contains("Soil Health", ignoreCase = true) -> 
                        "The Global Soil Health Accord targets restoring topsoil organic carbon content to at least 3% by the year 2040 through cover cropping and phasing out chemical fertilizers."
                    else -> "This correct answer is backed by critical exam-relevant parameters detailed in our curated daily analytical summaries."
                }

                val articleId = findArticleIdForQuestion(question.questionText, digestItems)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = "EDITORIAL EXPLANATION",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (articleId != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Review full article summary →",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { onNavigateToSummary(articleId) }
                                .testTag("review_article_link")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Navigation Bottom Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentIndex > 0) {
                OutlinedButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Previous",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onNext,
                enabled = selectedOption != null,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (currentIndex == totalQuestions - 1) "Finish Assessment" else "Next Question",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuizResultsContent(
    result: QuizResult,
    questions: List<QuizQuestion>,
    answers: Map<Int, Int>,
    onRetake: () -> Unit
) {
    val scrollState = rememberScrollState()
    val score = result.score

    // Text-based 'Retention Insight' block that organizes performance by category
    val insightText = remember(questions, answers, score) {
        val correctCats = mutableSetOf<String>()
        val incorrectCats = mutableSetOf<String>()
        
        if (answers.isNotEmpty() && questions.size == answers.size) {
            for (i in questions.indices) {
                val q = questions[i]
                val ans = answers[i]
                val category = when (i) {
                    0 -> "Polity"
                    1 -> "Economy"
                    2 -> "Environment"
                    3 -> "Science"
                    4 -> "Environment"
                    else -> "General Affairs"
                }
                if (ans == q.correctOptionIndex) {
                    correctCats.add(category)
                } else {
                    incorrectCats.add(category)
                }
            }
        } else {
            // Reconstructed fallbacks to handle app lifecycle/restart state
            when (score) {
                5 -> {
                    correctCats.addAll(listOf("Polity", "Economy", "Environment", "Science"))
                }
                4 -> {
                    correctCats.addAll(listOf("Polity", "Economy", "Science"))
                    incorrectCats.add("Environment")
                }
                3 -> {
                    correctCats.addAll(listOf("Polity", "Economy"))
                    incorrectCats.addAll(listOf("Environment", "Science"))
                }
                2 -> {
                    correctCats.add("Polity")
                    incorrectCats.addAll(listOf("Economy", "Environment", "Science"))
                }
                1 -> {
                    correctCats.add("Science")
                    incorrectCats.addAll(listOf("Polity", "Economy", "Environment"))
                }
                else -> {
                    incorrectCats.addAll(listOf("Polity", "Economy", "Environment", "Science"))
                }
            }
        }

        buildString {
            if (correctCats.isNotEmpty()) {
                append("Strong understanding in ")
                append(correctCats.joinToString(" and "))
                append(". ")
            }
            if (incorrectCats.isNotEmpty()) {
                if (correctCats.isNotEmpty()) append("\n\n")
                append("Consider skimming ")
                append(incorrectCats.joinToString(" and "))
                append(" again.")
            }
            if (correctCats.isEmpty() && incorrectCats.isEmpty()) {
                append("No data available. Complete the quiz to see your retention insight.")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Redesigned serene layout: clean, text-based block (No arcade scores, no arcade badges)
        Text(
            text = "EVALUATION OVERVIEW",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Assessment Completed Successfully",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "We prioritize deep conceptual understanding and focus over game-style metrics. Below is your structured evaluation insight based on today's topics.",
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Text-based Retention Insight Block Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("retention_insight_block"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "RETENTION INSIGHT",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = insightText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp,
                        fontFamily = FontFamily.Serif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Retake evaluation button
        Button(
            onClick = onRetake,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Retake Evaluation", fontWeight = FontWeight.Bold)
        }
    }
}

// Helper to resolve corresponding article from seed questions
private fun findArticleIdForQuestion(questionText: String, digestItems: List<DigestItem>): Long? {
    val keyword = when {
        questionText.contains("Municipal", ignoreCase = true) -> "Decentralized Governance"
        questionText.contains("Repo Rate", ignoreCase = true) || questionText.contains("Monetary policy", ignoreCase = true) -> "Central Bank Pauses"
        questionText.contains("Ramsar", ignoreCase = true) || questionText.contains("wetland", ignoreCase = true) -> "Wetland Initiative"
        questionText.contains("quantum", ignoreCase = true) || questionText.contains("Kelvin", ignoreCase = true) -> "Quantum Engine"
        questionText.contains("Soil Health", ignoreCase = true) -> "Soil Health Accord"
        else -> null
    } ?: return null
    return digestItems.find { it.headline.contains(keyword, ignoreCase = true) }?.id
}

// Data class to hold precise options coloring
private data class OptionColors(
    val containerClr: Color,
    val borderClr: Color,
    val borderStrokeWidth: androidx.compose.ui.unit.Dp,
    val contentClr: Color,
    val indicatorBg: Color,
    val indicatorTextClr: Color
)
