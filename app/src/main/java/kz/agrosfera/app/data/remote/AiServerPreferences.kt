package kz.agrosfera.app.data.remote

import android.content.Context
import android.content.SharedPreferences
import kz.agrosfera.app.BuildConfig

class AiServerPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBaseUrl(): String {
        val defaultUrl = normalize(BuildConfig.API_BASE_URL)
        val saved = prefs.getString(KEY_BASE_URL, null)?.trim()
        if (saved.isNullOrBlank()) return defaultUrl
        val normalized = normalize(saved)
        // Ескі қате IP (мыс. hotspot) сақталса — local.properties/build-тегі жаңасын алу
        if (normalized != defaultUrl && saved.contains("172.20.10.2")) {
            prefs.edit().putString(KEY_BASE_URL, defaultUrl).apply()
            return defaultUrl
        }
        return normalized
    }

    fun saveBaseUrl(url: String) {
        prefs.edit().putString(KEY_BASE_URL, normalize(url)).apply()
    }

    private fun normalize(url: String): String {
        var value = url.trim()
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "http://$value"
        }
        return value.trimEnd('/')
    }

    companion object {
        private const val PREFS_NAME = "agro_ai_server"
        private const val KEY_BASE_URL = "base_url"
    }
}
