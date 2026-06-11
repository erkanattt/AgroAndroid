package kz.agrosfera.app.domain.auth

data class UserSession(
    val name: String,
    val email: String,
    val phone: String = "",
)
