package com.example

import com.example.data.Category
import com.example.data.DigestItem
import com.example.data.NewsSyncManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test for fix #3: the generated (Gemini-invented) fallback must only kick in
 * when the live news pipeline returns nothing at all. A short list of real articles
 * is always preferred over a full list of fictional ones for an exam-prep app.
 *
 * Plain JUnit — shouldUseGeneratedFallback() is a pure function, no Android/Robolectric.
 */
class NewsSyncFallbackTest {

    private fun items(count: Int): List<DigestItem> = (1..count).map {
        DigestItem(
            category = Category.Other,
            headline = "Story $it",
            previewText = "",
            contextText = "",
            keyPointsText = "",
            whyItMattersText = "",
            examAngleText = "",
            sourceAFraming = "",
            sourceBFraming = "",
            sourceCFraming = "",
            date = "2026-09-07"
        )
    }

    @Test
    fun `empty live list uses generated fallback`() {
        assertTrue(NewsSyncManager.shouldUseGeneratedFallback(emptyList()))
    }

    @Test
    fun `single live item does not use generated fallback`() {
        assertFalse(NewsSyncManager.shouldUseGeneratedFallback(items(1)))
    }

    @Test
    fun `twelve live items do not use generated fallback`() {
        assertFalse(NewsSyncManager.shouldUseGeneratedFallback(items(12)))
    }

    @Test
    fun `twenty live items do not use generated fallback`() {
        assertFalse(NewsSyncManager.shouldUseGeneratedFallback(items(20)))
    }
}
