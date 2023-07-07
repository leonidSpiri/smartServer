package ru.spiridonov.smartserver.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

@Entity
@Table(name = "security")
data class Security(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotBlank
    val dateTime: OffsetDateTime,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @NotBlank
    val isSecurityTurnOn: Boolean = true
)