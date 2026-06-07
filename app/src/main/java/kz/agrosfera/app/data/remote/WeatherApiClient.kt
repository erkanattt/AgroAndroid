package kz.agrosfera.app.data.remote

import java.io.IOException
import java.util.concurrent.TimeUnit
import kz.agrosfera.app.domain.weather.KazakhstanCity
import kz.agrosfera.app.domain.weather.WeatherInfo
import kz.agrosfera.app.util.WeatherCodeMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class WeatherApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun fetchCurrent(city: KazakhstanCity): WeatherInfo {
        val url = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${city.latitude}&longitude=${city.longitude}")
            append("&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m")
            append("&timezone=Asia%2FAlmaty&forecast_days=1")
        }
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("weather_http_${response.code}")
            }
            val json = JSONObject(response.body?.string().orEmpty())
            val current = json.getJSONObject("current")
            val condition = WeatherCodeMapper.map(current.getInt("weather_code"))
            return WeatherInfo(
                cityName = city.name,
                temperatureC = current.getDouble("temperature_2m").toInt(),
                humidityPercent = current.getInt("relative_humidity_2m"),
                windSpeedMs = current.getDouble("wind_speed_10m"),
                conditionLabel = condition.label,
                conditionEmoji = condition.emoji,
            )
        }
    }
}
