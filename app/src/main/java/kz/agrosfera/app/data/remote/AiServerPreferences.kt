package kz.agrosfera.app.data.remote

import kz.agrosfera.app.BuildConfig

class AiServerPreferences {

    fun getBaseUrl(): String = normalize(BuildConfig.API_BASE_URL)

    private fun normalize(url: String): String {
        var value = url.trim()
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "http://$value"
        }
        return value.trimEnd('/')
    }

}
