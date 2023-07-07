package ru.spiridonov.smartserver.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import ru.spiridonov.smartserver.model.enums.DevTypes

@Entity
@Table(name = "rasp_devices")
data class RaspDevices(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    val devType: DevTypes,

    @NotBlank
    val description: String,

    @Column(nullable = false, unique = true)
    @NotBlank
    val pinId: Int
)
