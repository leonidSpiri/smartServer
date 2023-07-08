package ru.spiridonov.smartserver.payload.response

data class JwtResponse(
    val accessToken: String,
    val refreshToken:String,
    val tokenType: String = "Bearer",
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
)