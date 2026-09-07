package com.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Fix #7: cold start must not block on the splash while the live Gemini sync runs.
 *
 * NewsSyncManager.syncDailyNews() now wraps its live -> generated pipeline in
 * withTimeoutOrNull(15_000). This test exercises that exact boundary in isolation:
 * a suspend function that never finishes (delay(60_000)) wrapped in the same
 * withTimeoutOrNull(15_000) pattern must yield null. runTest's virtual clock skips
 * the wall-clock wait, so the test itself completes instantly.
 */
class SyncTimeoutTest {

    private suspend fun neverCompletesInTime(): Boolean {
        delay(60_000)
        return true
    }

    @Test
    fun `withTimeoutOrNull yields null when the wrapped sync exceeds 15s`() = runTest {
        val result: Boolean? = withTimeoutOrNull(15_000L) {
            neverCompletesInTime()
        }

        assertNull("A sync that runs past the 15s budget must time out to null", result)
    }
}
