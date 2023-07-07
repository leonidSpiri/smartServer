package ru.spiridonov.smartserver.service

import org.springframework.stereotype.Service
import ru.spiridonov.smartserver.repository.RaspDevicesRepository

@Service
class RaspDevicesService(val raspDevicesRepository: RaspDevicesRepository) {
}