package ru.spiridonov.smartserver.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

@Entity
@Table(name = "rasp_state")
data class RaspState(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotBlank
    val dateTime: OffsetDateTime,

    @NotBlank
    val fanWorks: Boolean,

    @NotBlank
    val conditionerWorks: Boolean,

    @NotBlank
    val tempSensor: Int,

    @NotBlank
    val boxTempSensor: Int,

    val isSecurityViolated: Boolean = false
)