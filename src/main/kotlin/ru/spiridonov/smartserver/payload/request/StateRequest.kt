package ru.spiridonov.smartserver.payload.request

import jakarta.validation.constraints.NotBlank

data class StateRequest(
    @NotBlank
    val newRequiredState: String,

    val isSecurityViolated:Boolean = false
)
