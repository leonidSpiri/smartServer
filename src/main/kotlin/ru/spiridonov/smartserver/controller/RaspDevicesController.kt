package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.model.RaspDevices
import ru.spiridonov.smartserver.model.enums.DevTypes
import ru.spiridonov.smartserver.payload.request.RaspDeviceRequest
import ru.spiridonov.smartserver.repository.RaspDevicesRepository

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/rasp_dev")
class RaspDevicesController(
    val raspDevicesRepository: RaspDevicesRepository
) {
    @GetMapping
    fun getRaspDevices(): MutableList<RaspDevices> = raspDevicesRepository.findAll()

    @PostMapping
    fun addRaspDevice(@Valid @RequestBody request: RaspDeviceRequest): ResponseEntity<*> {
        val devType = DevTypes.values().find { it.name == request.devType }
            ?: return ResponseEntity.badRequest().body("Device type ${request.devType} not found")

        val raspDev = RaspDevices(
            devType = devType,
            pinId = request.pinId,
            description = request.description
        )
        val savedDev = raspDevicesRepository.save(raspDev)
        return ResponseEntity.ok(savedDev)
    }
}