package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.smartserver.model.RaspDevices

interface RaspDevicesRepository : JpaRepository<RaspDevices, Long> {
}