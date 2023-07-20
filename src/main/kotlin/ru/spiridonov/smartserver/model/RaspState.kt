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
    val raspState: String,

    val isSecurityViolated:Boolean = false
)