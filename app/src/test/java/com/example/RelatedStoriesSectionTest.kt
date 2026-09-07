package com.example

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.Category
import com.example.data.DigestItem
import com.example.ui.screens.RelatedStoriesSection
import com.example.ui.theme.DigestTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P2 batch 2 Feature 3 — "Related to a story you read" section. Same Compose-under-
 * Robolectric approach as ReviewScreenTest; drives the stateless RelatedStoriesSection
 * directly.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RelatedStoriesSectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun story(id: Long, headline: String, date: String) = DigestItem(
        id = id,
        category = Category.Economy,
        headline = headline,
        previewText = "", contextText = "", keyPointsText = "",
        whyItMattersText = "", examAngleText = "",
        sourceAFraming = "", sourceBFraming = "", sourceCFraming = "",
        date = date
    )

    @Test
    fun `renders nothing when there are no related stories`() {
        composeTestRule.setContent {
            DigestTheme {
                RelatedStoriesSection(stories = emptyList(), onOpenStory = {})
            }
        }

        composeTestRule.onAllNodesWithText("RELATED TO STORIES YOU'VE READ").assertCountEquals(0)
    }

    @Test
    fun `renders related headlines and a tap navigates to that story`() {
        var opened: Long? = null

        composeTestRule.setContent {
            DigestTheme {
                RelatedStoriesSection(
                    stories = listOf(
                        story(11, "RBI holds the repo rate steady", "2026-08-15"),
                        story(22, "GST collections touch a record high", "2026-08-01")
                    ),
                    onOpenStory = { opened = it }
                )
            }
        }

        composeTestRule.onNodeWithText("RBI holds the repo rate steady").assertIsDisplayed()
        composeTestRule.onNodeWithText("GST collections touch a record high").assertIsDisplayed()

        composeTestRule.onNodeWithText("RBI holds the repo rate steady").performClick()
        assertEquals(11L, opened)
    }

    @Test
    fun `caps the list at three related stories`() {
        composeTestRule.setContent {
            DigestTheme {
                RelatedStoriesSection(
                    stories = (1L..5L).map { story(it, "Economy story $it", "2026-08-0$it") },
                    onOpenStory = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Economy story 3").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("Economy story 4").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("Economy story 5").assertCountEquals(0)
    }
}
