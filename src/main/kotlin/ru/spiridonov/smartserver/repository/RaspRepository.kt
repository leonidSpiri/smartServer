package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.smartserver.model.Rasp

interface RaspRepository : JpaRepository<Rasp, Long> {
}