package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.model.Mobile
import ru.spiridonov.smartserver.model.enums.DevTypes
import ru.spiridonov.smartserver.payload.request.MobileRequest
import ru.spiridonov.smartserver.payload.response.MessageResponse
import ru.spiridonov.smartserver.repository.MobileRepository
import ru.spiridonov.smartserver.repository.RaspDevicesRepository
import ru.spiridonov.smartserver.repository.UserRepository
import ru.spiridonov.smartserver.service.UserDetailsImpl
import java.time.OffsetDateTime

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/mobile")
class MobileController(
    val raspDevicesRepository: RaspDevicesRepository,
    val mobileRepository: MobileRepository,
    val userRepository: UserRepository
) {

    @PostMapping
    fun mobileRequest(@Valid @RequestBody request: MobileRequest): ResponseEntity<*> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        val user = userRepository.findByEmail(userDetails.getEmail())
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "User not found"))

        val statePairs = mutableListOf<Pair<Int, String>>()
        request.newRequiredState.split(",").forEach { state ->
            val list = state.split(":").map { it.trim() }
            DevTypes.values().forEach { devType ->
                if (devType.name == list[0]) {
                    val raspDev = raspDevicesRepository.findByDevType(devType)
                    if (raspDev != null)
                        statePairs.add(Pair(raspDev.pinId, list[1]))
                    else
                        return ResponseEntity.badRequest()
                            .body(MessageResponse(message = "Device ${devType.name} not found"))
                }
            }
        }

        val savedRequest = mobileRepository.save(
            Mobile(
                dateTime = OffsetDateTime.now(),
                user = user,
                newRequiredState = statePairs.toMap().toString().replace("{", "").replace("}", "").replace("=", ":")
            )
        )

        return ResponseEntity.ok(savedRequest)
    }

    @GetMapping("/all_requests")
    fun allRequests(): ResponseEntity<*> {
        return ResponseEntity.ok(mobileRepository.findAll())
    }

    @GetMapping("/last_request")
    fun lastRequest(): ResponseEntity<*> {
        return ResponseEntity.ok(mobileRepository.findTopByOrderByDateTimeDesc())
    }
}