package kz.agrosfera.app.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<UserSession?>

    suspend fun register(name: String, email: String, password: String, phone: String = ""): Result<Unit>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun logout()
}
