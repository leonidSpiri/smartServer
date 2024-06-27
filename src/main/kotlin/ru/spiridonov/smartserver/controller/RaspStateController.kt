package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.model.RaspState
import ru.spiridonov.smartserver.model.enums.DevTypes
import ru.spiridonov.smartserver.payload.request.StateRequest
import ru.spiridonov.smartserver.payload.response.MessageResponse
import ru.spiridonov.smartserver.repository.RaspDevicesRepository
import ru.spiridonov.smartserver.repository.RaspStateRepository
import ru.spiridonov.smartserver.repository.SecurityRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/rasp_state")
class RaspStateController(
    val raspDevicesRepository: RaspDevicesRepository,
    val raspStateRepository: RaspStateRepository,
    val securityRepository: SecurityRepository
) {
    @PostMapping
    fun raspResponse(@Valid @RequestBody request: StateRequest): ResponseEntity<*> {
        var fanWorks = false
        var conditionerWorks = false
        var tempSensor = 0
        var boxTempSensor = 0

        request.newRequiredState.split(",").forEach { state ->
            val list = state.split(":").map { it.trim() }
            val devType = raspDevicesRepository.findByPinId(list[0].toInt())?.devType
                ?: return ResponseEntity.badRequest()
                    .body(MessageResponse(message = "Device with pin ${list[0]} not found"))
            when (devType) {
                DevTypes.FAN -> fanWorks = list[1].toBoolean()
                DevTypes.CONDITIONER -> conditionerWorks = list[1].toBoolean()
                DevTypes.TEMP_SENSOR -> tempSensor = list[1].toInt()
                DevTypes.BOX_TEMP_SENSOR -> boxTempSensor = list[1].toInt()
                DevTypes.LIGHT -> TODO()
                DevTypes.TEMP_HUMIDITY_SENSOR -> TODO()
                DevTypes.SECURITY_SENSOR -> TODO()
            }
        }

        val savedRequest = raspStateRepository.save(
            RaspState(
                dateTime = OffsetDateTime.now(ZoneOffset.of("+03:00")),
                fanWorks = fanWorks,
                conditionerWorks = conditionerWorks,
                tempSensor = tempSensor,
                boxTempSensor = boxTempSensor,
                isSecurityViolated = request.isSecurityViolated
            )
        )

        return ResponseEntity.ok(savedRequest)
    }

    @GetMapping("/all_responses")
    fun allResponse(): ResponseEntity<List<RaspState>> {
        return ResponseEntity.ok(raspStateRepository.findAll())
    }

    @GetMapping("/all_responses/{date}")
    fun allResponseByDate(@PathVariable date: String): ResponseEntity<List<RaspState>> {
        val startDate = OffsetDateTime.parse("${date}T00:00:00Z")
        val endDate = OffsetDateTime.parse("${date}T23:59:59Z")
        return ResponseEntity.ok(raspStateRepository.findAllByDateTimeBetweenOrderByDateTimeAsc(startDate, endDate))
    }

    @GetMapping("/last_response")
    fun lastResponse(): ResponseEntity<RaspState> {
        val isSecurityTurnOn = securityRepository.findTopByOrderByDateTimeDesc()?.isSecurityTurnOn ?: true
        val state = raspStateRepository.findTopByOrderByDateTimeDesc()
        val newState = if (!isSecurityTurnOn) state?.copy(isSecurityViolated = false) else state
        return ResponseEntity.ok(newState)
    }
}