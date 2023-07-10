package ru.spiridonov.smartserver.payload.request

import jakarta.validation.constraints.NotBlank

data class MobileRequest(
    @NotBlank
    val newRequiredState: String
)
