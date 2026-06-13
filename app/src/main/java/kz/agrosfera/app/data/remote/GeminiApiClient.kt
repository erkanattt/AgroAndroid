package kz.agrosfera.app.data.remote

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kz.agrosfera.app.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class GeminiApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun models(): List<String> {
        val primary = BuildConfig.GEMINI_MODEL.trim().ifBlank { "gemini-2.5-flash" }
        val fallback = "gemini-2.5-flash-lite"
        return if (primary == fallback) listOf(primary) else listOf(primary, fallback)
    }

    fun isConfigured(): Boolean = BuildConfig.GEMINI_API_KEY.isNotBlank()

    fun sendMessage(userMessage: String, history: List<Pair<String, String>>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            throw IOException("gemini_key_missing")
        }

        var lastError: IOException? = null
        for (model in models()) {
            try {
                return execute(model, apiKey, userMessage, history)
            } catch (e: IOException) {
                lastError = e
                if (!isRetryableModelError(e)) break
            }
        }
        throw lastError ?: IOException("gemini_empty")
    }

    private fun execute(
        model: String,
        apiKey: String,
        userMessage: String,
        history: List<Pair<String, String>>,
    ): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

        val contents = JSONArray()
        history.forEach { (role, text) ->
            contents.put(
                JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(JSONObject().put("text", text))),
            )
        }
        contents.put(
            JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", userMessage))),
        )

        val body = JSONObject()
            .put(
                "system_instruction",
                JSONObject().put(
                    "parts",
                    JSONArray().put(
                        JSONObject().put(
                            "text",
                            "Сен AgroSphere агроном көмекшісісің. " +
                                "Қазақ тілінде жауап бер. Өсімдік аурулары, алдын алу, " +
                                "егістік жұмыстары туралы нақты әрі қысқа кеңес бер.",
                        ),
                    ),
                ),
            )
            .put("contents", contents)
            .toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("x-goog-api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw mapHttpError(response.code, raw)
                }
                return parseSuccess(raw)
            }
        } catch (e: UnknownHostException) {
            throw IOException("gemini_network_host")
        } catch (e: ConnectException) {
            throw IOException("gemini_network_connect")
        } catch (e: SocketTimeoutException) {
            throw IOException("gemini_network_timeout")
        }
    }

    private fun parseSuccess(raw: String): String {
        val json = JSONObject(raw)
        val error = json.optJSONObject("error")
        if (error != null) {
            throw IOException(error.optString("message", "gemini_api_error"))
        }
        val candidates = json.optJSONArray("candidates")
        if (candidates == null || candidates.length() == 0) {
            throw IOException("gemini_empty")
        }
        val parts = candidates.getJSONObject(0)
            .optJSONObject("content")
            ?.optJSONArray("parts")
        if (parts == null || parts.length() == 0) {
            throw IOException("gemini_empty")
        }
        val text = parts.getJSONObject(0).optString("text").trim()
        if (text.isBlank()) throw IOException("gemini_empty")
        return text
    }

    private fun mapHttpError(code: Int, raw: String): IOException {
        val apiMessage = try {
            JSONObject(raw).optJSONObject("error")?.optString("message")
        } catch (_: Exception) {
            null
        }
        when (code) {
            403 -> return IOException("gemini_api_forbidden")
            429 -> return IOException("gemini_api_busy")
            503 -> return IOException("gemini_api_busy")
        }
        if (apiMessage != null) {
            if (apiMessage.contains("high demand", ignoreCase = true)) {
                return IOException("gemini_api_busy")
            }
            if (apiMessage.contains("no longer available", ignoreCase = true)) {
                return IOException("gemini_model_deprecated")
            }
            return IOException(apiMessage.take(200))
        }
        return IOException("gemini_http_$code")
    }

    private fun isRetryableModelError(error: IOException): Boolean {
        val msg = error.message.orEmpty()
        return msg == "gemini_api_busy" ||
            msg == "gemini_model_deprecated" ||
            msg.contains("no longer available", ignoreCase = true) ||
            msg.contains("high demand", ignoreCase = true)
    }
}
