package com.example

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Source-scan test for fix #4: the Gemini model alias must not be hardcoded as the
 * pinned string "gemini-3.5-flash" anywhere in main source. It now lives in a single
 * `GEMINI_MODEL` constant per file (NewsSyncManager.kt, GeminiTranslationClient.kt).
 *
 * This is deliberately a plain-text scan of the actual source files, NOT a network
 * test. We do not call the real Gemini API from unit tests; we only assert that the
 * decommissioned/pinned model string has been removed from the codebase.
 */
class GeminiModelConstantTest {

    private val sourceFiles = listOf(
        "src/main/java/com/example/data/NewsSyncManager.kt",
        "src/main/java/com/example/utils/GeminiTranslationClient.kt"
    )

    private fun resolve(relativePath: String): File {
        // Tests run with the working directory set to the module (app/) directory.
        val direct = File(relativePath)
        return if (direct.exists()) direct else File("app", relativePath)
    }

    @Test
    fun `no pinned gemini-3_5-flash string appears in main source`() {
        val stalePattern = Regex("gemini-3\\.5")

        for (relativePath in sourceFiles) {
            val file = resolve(relativePath)
            assertTrue("Expected source file to exist: ${file.absolutePath}", file.exists())

            val contents = file.readText()
            assertFalse(
                "Found a pinned Gemini model string (gemini-3.5...) in $relativePath — " +
                    "it should use the GEMINI_MODEL constant instead.",
                contents.contains("gemini-3.5-flash") || stalePattern.containsMatchIn(contents)
            )
        }
    }
}
