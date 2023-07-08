package ru.spiridonov.smartserver.payload.response

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)
