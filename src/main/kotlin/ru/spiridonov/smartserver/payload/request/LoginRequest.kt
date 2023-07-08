package ru.spiridonov.smartserver.payload.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @NotBlank
    val userName: String,
    @NotBlank
    val password: String
)
