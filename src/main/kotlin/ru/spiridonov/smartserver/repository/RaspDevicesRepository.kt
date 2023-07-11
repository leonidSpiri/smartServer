package ru.spiridonov.smartserver.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.smartserver.model.RaspDevices
import ru.spiridonov.smartserver.model.enums.DevTypes

interface RaspDevicesRepository : JpaRepository<RaspDevices, Long> {
    fun findByDevType(devType: DevTypes): RaspDevices?

    fun findByPinId(pinId: Int): RaspDevices?
}