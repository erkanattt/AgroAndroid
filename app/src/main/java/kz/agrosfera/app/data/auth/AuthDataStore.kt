package kz.agrosfera.app.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kz.agrosfera.app.domain.auth.AuthRepository
import kz.agrosfera.app.domain.auth.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "agro_auth")

class AuthRepositoryImpl(
    context: Context,
) : AuthRepository {

    private val store = context.applicationContext.authDataStore

    private object Keys {
        val name = stringPreferencesKey("name")
        val email = stringPreferencesKey("email")
        val phone = stringPreferencesKey("phone")
        val password = stringPreferencesKey("password")
        val loggedIn = booleanPreferencesKey("logged_in")
    }

    override val session: Flow<UserSession?> = store.data.map { prefs ->
        val logged = prefs[Keys.loggedIn] == true
        val email = prefs[Keys.email]
        val name = prefs[Keys.name]
        if (!logged || email.isNullOrBlank() || name.isNullOrBlank()) null
        else UserSession(
            name = name,
            email = email,
            phone = prefs[Keys.phone].orEmpty(),
        )
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
    ): Result<Unit> {
        store.edit { prefs ->
            prefs[Keys.name] = name.trim()
            prefs[Keys.email] = email.trim().lowercase()
            prefs[Keys.phone] = phone.trim()
            prefs[Keys.password] = password
            prefs[Keys.loggedIn] = true
        }
        return Result.success(Unit)
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        val normalized = email.trim().lowercase()
        val snapshot = store.data.first()
        val storedEmail = snapshot[Keys.email]
        val storedPass = snapshot[Keys.password]
        if (storedEmail == normalized && storedPass == password) {
            store.edit { prefs -> prefs[Keys.loggedIn] = true }
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("invalid"))
    }

    override suspend fun logout() {
        store.edit { prefs ->
            prefs[Keys.loggedIn] = false
        }
    }
}
