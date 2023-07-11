package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.smartserver.model.RaspState

interface RaspStateRepository : JpaRepository<RaspState, Long> {
    fun findTopByOrderByDateTimeDesc(): RaspState?
}