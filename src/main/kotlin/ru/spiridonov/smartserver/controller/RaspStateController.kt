package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.model.RaspState
import ru.spiridonov.smartserver.payload.request.StateRequest
import ru.spiridonov.smartserver.payload.response.MessageResponse
import ru.spiridonov.smartserver.repository.RaspDevicesRepository
import ru.spiridonov.smartserver.repository.RaspStateRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/rasp_state")
class RaspStateController(
    val raspDevicesRepository: RaspDevicesRepository,
    val raspStateRepository: RaspStateRepository
) {
    @PostMapping
    fun raspResponse(@Valid @RequestBody request: StateRequest): ResponseEntity<*> {
       val statePairs = mutableListOf<Pair<String, String>>()
        request.newRequiredState.split(",").forEach { state ->
            val list = state.split(":").map { it.trim() }
            val devType = raspDevicesRepository.findByPinId(list[0].toInt())?.devType
                ?: return ResponseEntity.badRequest()
                    .body(MessageResponse(message = "Device with pin ${list[0]} not found"))
            statePairs.add(Pair(devType.name, list[1]))
        }
        if (statePairs.isEmpty())
            return ResponseEntity.badRequest()
                .body(MessageResponse(message = "No devices found"))

        val savedRequest = raspStateRepository.save(
            RaspState(
                dateTime = OffsetDateTime.now(ZoneOffset.of("+03:00")),
                raspState = statePairs.toMap().toString().replace("{", "").replace("}", "").replace("=", ":"),
                isSecurityViolated = request.isSecurityViolated
            )
        )

        return ResponseEntity.ok(savedRequest)
    }

    @GetMapping("/all_responses")
    fun allResponse(): ResponseEntity<*> {
        return ResponseEntity.ok(raspStateRepository.findAll())
    }

    @GetMapping("/last_response")
    fun lastResponse(): ResponseEntity<*> {
        return ResponseEntity.ok(raspStateRepository.findTopByOrderByDateTimeDesc())
    }
}