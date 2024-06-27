package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.config.Config
import ru.spiridonov.smartserver.model.Mobile
import ru.spiridonov.smartserver.model.enums.DevTypes
import ru.spiridonov.smartserver.payload.request.MobileStateRequest
import ru.spiridonov.smartserver.payload.response.MessageResponse
import ru.spiridonov.smartserver.repository.MobileRepository
import ru.spiridonov.smartserver.repository.RaspDevicesRepository
import ru.spiridonov.smartserver.repository.RaspStateRepository
import ru.spiridonov.smartserver.repository.UserRepository
import ru.spiridonov.smartserver.service.UserDetailsImpl
import java.util.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/mobile")
class MobileController(
    val raspDevicesRepository: RaspDevicesRepository,
    val raspStateRepository: RaspStateRepository,
    val mobileRepository: MobileRepository,
    val userRepository: UserRepository
) {

    @PostMapping
    fun mobileRequest(@Valid @RequestBody request: MobileStateRequest): ResponseEntity<*> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        val user = userRepository.findByEmail(userDetails.getEmail())
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "User not found"))

        val savedRequest = mobileRepository.save(
            Mobile(
                dateTime = Date(Date().time + 3 * 60 * 60 * 1000),
                userId = user.id ?: return ResponseEntity.badRequest()
                    .body(MessageResponse(message = "User not found")),
                newRequiredState = request.newRequiredState
            )
        )

        return ResponseEntity.ok(savedRequest)
    }

    //TODO filter by date
    @GetMapping("/all_requests")
    fun allRequests(): ResponseEntity<*> {
        return ResponseEntity.ok(mobileRepository.findAll())
    }

    @GetMapping("/required_temp")
    fun getRequiredTemp(): ResponseEntity<*> {
        val request = mobileRepository.findTopByOrderByDateTimeDesc()
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "No requests found"))
        return ResponseEntity.ok(request.newRequiredState.tempSensor)
    }

    @GetMapping("/last_request")
    fun lastRequest(): ResponseEntity<*> {
        val request = mobileRepository.findTopByOrderByDateTimeDesc()
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "No mobile state requests found"))
        val prevState = raspStateRepository.findTopByOrderByDateTimeDesc()
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "No rasp state requests found"))

        val needTemp = request.newRequiredState.tempSensor
        val realTemp = prevState.tempSensor

        var newFanState = false
        var newCondState = false

        if (request.newRequiredState.conditioner)
            newCondState = true
        else if (request.newRequiredState.fan)
            newFanState = true
        else {
            if (realTemp > needTemp)
                newFanState = true
            if (realTemp >= (Config.COND_DELTA + needTemp))
                newCondState = true

            if (prevState.fanWorks && realTemp >= (needTemp - Config.FAN_DELTA))
                newFanState = true

            if (prevState.conditionerWorks && realTemp >= (needTemp - Config.COND_DELTA))
                newCondState = true
        }

        if (newCondState) newFanState = true

        val fanPinId = raspDevicesRepository.findByDevType(DevTypes.FAN)?.pinId
        val condPinId = raspDevicesRepository.findByDevType(DevTypes.CONDITIONER)?.pinId
        val finalState = "$fanPinId:$newFanState, $condPinId:$newCondState"

        return ResponseEntity.ok(finalState)
    }
}