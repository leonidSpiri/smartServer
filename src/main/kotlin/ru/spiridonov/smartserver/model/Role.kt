package ru.spiridonov.smartserver.model

import jakarta.persistence.*
import ru.spiridonov.smartserver.model.enums.Roles

@Entity
@Table(name = "roles")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Enumerated(EnumType.STRING)
    val role: Roles
)