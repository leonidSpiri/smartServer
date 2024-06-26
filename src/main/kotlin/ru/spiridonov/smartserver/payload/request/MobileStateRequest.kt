package ru.spiridonov.smartserver.payload.request

import jakarta.validation.constraints.NotBlank
import ru.spiridonov.smartserver.model.MobileRequirements

data class MobileStateRequest(
    @NotBlank
    val newRequiredState: MobileRequirements,

    val isSecurityViolated: Boolean = false
)
