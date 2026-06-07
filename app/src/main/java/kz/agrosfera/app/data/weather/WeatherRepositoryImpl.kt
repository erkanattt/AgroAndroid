package kz.agrosfera.app.data.weather

import kz.agrosfera.app.data.local.WeatherPreferences
import kz.agrosfera.app.data.remote.WeatherApiClient
import kz.agrosfera.app.domain.weather.KazakhstanCityCatalog
import kz.agrosfera.app.domain.weather.WeatherInfo
import kz.agrosfera.app.domain.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl(
    private val preferences: WeatherPreferences,
    private val apiClient: WeatherApiClient,
) : WeatherRepository {

    override val selectedCityId: Flow<String> = preferences.selectedCityId

    override suspend fun getSelectedCityId(): String = preferences.getSelectedCityId()

    override suspend fun setSelectedCityId(cityId: String) {
        preferences.setSelectedCityId(cityId)
    }

    override suspend fun fetchCurrentWeather(cityId: String): Result<WeatherInfo> =
        withContext(Dispatchers.IO) {
            val city = KazakhstanCityCatalog.byId(cityId) ?: KazakhstanCityCatalog.defaultCity()
            runCatching { apiClient.fetchCurrent(city) }
        }
}
