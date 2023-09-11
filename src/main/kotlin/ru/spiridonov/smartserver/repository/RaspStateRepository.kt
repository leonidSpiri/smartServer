package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.smartserver.model.RaspState
import java.time.OffsetDateTime

interface RaspStateRepository : JpaRepository<RaspState, Long> {
    fun findTopByOrderByDateTimeDesc(): RaspState?

    fun findAllByDateTimeBetweenOrderByDateTimeAsc(from: OffsetDateTime, to: OffsetDateTime): List<RaspState>
}