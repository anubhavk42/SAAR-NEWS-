package com.example

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.example.data.QuizQuestion
import com.example.ui.screens.ReviewScreenContent
import com.example.ui.theme.DigestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P2 Feature 3 — Review tab UI. Follows the existing Compose-under-Robolectric pattern
 * from GreetingScreenshotTest (createComposeRule + setContent), but with assertions
 * instead of a screenshot. Drives the stateless ReviewScreenContent directly, the same
 * way GreetingScreenshotTest drives OnboardingScreen — no ViewModel needed.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun question(text: String) = QuizQuestion(
        id = 1,
        date = "2026-09-07",
        questionText = text,
        option1 = "UPSC", option2 = "SSC", option3 = "RBI", option4 = "NITI Aayog",
        correctOptionIndex = 0
    )

    @Test
    fun `empty state renders when there are no due questions`() {
        composeTestRule.setContent {
            DigestTheme {
                ReviewScreenContent(
                    questions = emptyList(),
                    loaded = true,
                    currentIndex = 0,
                    answers = emptyMap(),
                    digestItems = emptyList(),
                    onOptionSelected = { _, _ -> },
                    onNext = {},
                    onPrevious = {},
                    onNavigateToSummary = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Nothing to review today").assertIsDisplayed()
    }

    @Test
    fun `due questions render in the answering UI`() {
        composeTestRule.setContent {
            DigestTheme {
                ReviewScreenContent(
                    questions = listOf(question("Which body conducts the civil services exam?")),
                    loaded = true,
                    currentIndex = 0,
                    answers = emptyMap(),
                    digestItems = emptyList(),
                    onOptionSelected = { _, _ -> },
                    onNext = {},
                    onPrevious = {},
                    onNavigateToSummary = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Which body conducts the civil services exam?").assertIsDisplayed()
        // All four options are laid out in the answering UI (some may sit below the
        // fold in the test viewport, so match on presence rather than visibility).
        composeTestRule.onAllNodesWithText("NITI Aayog").assertCountEquals(1)
    }
}
