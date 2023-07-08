package ru.spiridonov.smartserver.payload.request

import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

data class MobileRequest(
    @NotBlank
    val dateTime: OffsetDateTime,

    @NotBlank
    val newRequiredState: String
)
