package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.smartserver.model.Security

interface SecurityRepository : JpaRepository<Security, Long> {
    fun findTopByOrderByDateTimeDesc(): Security?
}