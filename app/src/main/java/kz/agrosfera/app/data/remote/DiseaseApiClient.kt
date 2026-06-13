package kz.agrosfera.app.data.remote

import kz.agrosfera.app.BuildConfig
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private fun normalizeApiBaseUrl(url: String): String {
    var value = url.trim()
    if (!value.startsWith("http://") && !value.startsWith("https://")) {
        value = "http://$value"
    }
    return value.trimEnd('/')
}

class DiseaseApiClient(
    private val baseUrl: String = normalizeApiBaseUrl(BuildConfig.API_BASE_URL),
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    fun diagnose(imageBytes: ByteArray, filename: String): DiseaseApiResponse {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                filename,
                imageBytes.toRequestBody("image/*".toMediaType()),
            )
            .build()

        val url = baseUrl.trimEnd('/') + "/api/v1/disease"
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val message = parseErrorMessage(raw) ?: "HTTP ${response.code}"
                throw DiseaseApiException(message)
            }
            return parseSuccess(raw)
        }
    }

    private fun parseSuccess(raw: String): DiseaseApiResponse {
        val json = JSONObject(raw)
        if (!json.optBoolean("ok", false)) {
            throw DiseaseApiException(json.optString("error", "unknown_error"))
        }
        return DiseaseApiResponse(
            classId = json.getString("class_id"),
            confidencePercent = json.optInt("confidence_percent", -1).takeIf { it >= 0 },
            descriptionHtml = json.getString("description_html"),
        )
    }

    private fun parseErrorMessage(raw: String): String? = try {
        JSONObject(raw).optString("error").takeIf { it.isNotBlank() }
    } catch (_: Exception) {
        null
    }
}

data class DiseaseApiResponse(
    val classId: String,
    val confidencePercent: Int?,
    val descriptionHtml: String,
)

class DiseaseApiException(message: String) : IOException(message)
