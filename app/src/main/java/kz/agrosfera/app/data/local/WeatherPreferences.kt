package kz.agrosfera.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kz.agrosfera.app.domain.weather.KazakhstanCityCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.weatherDataStore: DataStore<Preferences> by preferencesDataStore(name = "agro_weather")

class WeatherPreferences(context: Context) {

    private val store = context.applicationContext.weatherDataStore

    private val cityIdKey = stringPreferencesKey("city_id")

    val selectedCityId: Flow<String> = store.data.map { prefs ->
        prefs[cityIdKey] ?: KazakhstanCityCatalog.defaultCity().id
    }

    suspend fun getSelectedCityId(): String =
        selectedCityId.first()

    suspend fun setSelectedCityId(cityId: String) {
        store.edit { prefs -> prefs[cityIdKey] = cityId }
    }
}
