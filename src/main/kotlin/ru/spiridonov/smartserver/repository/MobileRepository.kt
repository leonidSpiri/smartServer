package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.smartserver.model.Mobile

interface MobileRepository : JpaRepository<Mobile, Long> {
    fun findTopByOrderByDateTimeDesc(): Mobile?
}