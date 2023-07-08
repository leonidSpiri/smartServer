package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.model.enums.DevTypes
import ru.spiridonov.smartserver.payload.request.MobileRequest
import ru.spiridonov.smartserver.payload.response.MessageResponse
import ru.spiridonov.smartserver.service.UserDetailsImpl
import java.time.OffsetDateTime

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/mobile")
class MobileController {

    @GetMapping
    fun logoutUser(@Valid @RequestBody request: MobileRequest): ResponseEntity<*> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        val statePairs = mutableListOf<Pair<DevTypes, String>>()
        request.newRequiredState.split(",").forEach { state ->
            val list = state.split(":").map { it.trim() }
            DevTypes.values().forEach { devType ->
                if (devType.name == list[0]) {
                    statePairs.add(Pair(devType, list[1]))
                    //replace FAN to pinID
                }
            }
        }
        println(statePairs.toString())
        return ResponseEntity.ok(
            MessageResponse(
                message = userDetails.getId()
                    .toString() + userDetails.getEmail() + request.dateTime.year + statePairs.toString()
            )
        )
    }

    @GetMapping("/offsetDateTime")
    fun offsetDateTime(): ResponseEntity<*> {
        return ResponseEntity.ok(MessageResponse(message = "OffsetDateTime.now() = " + OffsetDateTime.now()))
    }
}