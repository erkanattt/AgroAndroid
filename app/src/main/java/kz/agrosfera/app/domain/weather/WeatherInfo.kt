package kz.agrosfera.app.domain.weather

data class WeatherInfo(
    val cityName: String,
    val temperatureC: Int,
    val humidityPercent: Int,
    val windSpeedMs: Double,
    val conditionLabel: String,
    val conditionEmoji: String,
)
