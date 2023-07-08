package ru.spiridonov.smartserver.payload.request

import jakarta.validation.constraints.NotBlank

data class TokenRefreshRequest(
    @NotBlank
    val refreshToken: String
)
