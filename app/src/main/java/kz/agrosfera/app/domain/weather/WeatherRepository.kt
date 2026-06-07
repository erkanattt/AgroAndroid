package kz.agrosfera.app.domain.weather

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    val selectedCityId: Flow<String>

    suspend fun getSelectedCityId(): String

    suspend fun setSelectedCityId(cityId: String)

    suspend fun fetchCurrentWeather(cityId: String): Result<WeatherInfo>
}
