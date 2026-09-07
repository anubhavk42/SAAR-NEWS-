package com.example.utils

import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiTranslationClient {

    private const val GEMINI_MODEL = "gemini-flash-latest"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun translateText(text: String, targetLanguage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext text
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent?key=$apiKey"

        val prompt = "You are an expert translator. Translate the following text completely into $targetLanguage. Deliver only the translated text. Do not add any introductory, meta or concluding remarks, explanations, or notes. If there are list items or headings, keep their structure intact.\n\nText to translate:\n$text"

        val requestJson = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext text
                }
                val bodyString = response.body?.string() ?: return@withContext text
                val responseJson = JSONObject(bodyString)
                val candidates = responseJson.optJSONArray("candidates") ?: return@withContext text
                val firstCandidate = candidates.optJSONObject(0) ?: return@withContext text
                val content = firstCandidate.optJSONObject("content") ?: return@withContext text
                val parts = content.optJSONArray("parts") ?: return@withContext text
                val firstPart = parts.optJSONObject(0) ?: return@withContext text
                val translated = firstPart.optString("text")
                if (translated.isNotEmpty()) {
                    translated.trim()
                } else {
                    text
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            text
        }
    }
}
