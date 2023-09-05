package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.apache.commons.lang3.tuple.MutablePair
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.config.Config
import ru.spiridonov.smartserver.model.Mobile
import ru.spiridonov.smartserver.model.RaspDevices
import ru.spiridonov.smartserver.model.enums.DevTypes
import ru.spiridonov.smartserver.payload.request.StateRequest
import ru.spiridonov.smartserver.payload.response.MessageResponse
import ru.spiridonov.smartserver.repository.MobileRepository
import ru.spiridonov.smartserver.repository.RaspDevicesRepository
import ru.spiridonov.smartserver.repository.RaspStateRepository
import ru.spiridonov.smartserver.repository.UserRepository
import ru.spiridonov.smartserver.service.UserDetailsImpl
import java.time.OffsetDateTime
import java.time.ZoneOffset

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
    fun mobileRequest(@Valid @RequestBody request: StateRequest): ResponseEntity<*> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        val user = userRepository.findByEmail(userDetails.getEmail())
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "User not found"))

        val savedRequest = mobileRepository.save(
            Mobile(
                dateTime = OffsetDateTime.now(ZoneOffset.of("+03:00")),
                user = user,
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
        val requiredTemp = request.newRequiredState.split(",").find { it.split(":")[0] == DevTypes.TEMP_SENSOR.toString() }
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "No required temp found"))
        return ResponseEntity.ok(requiredTemp.split(":")[1])
    }

    @GetMapping("/last_request")
    fun lastRequest(): ResponseEntity<*> {
        var needTemp = 0
        var realTemp = 0
        val statePairs = mutableListOf<MutablePair<RaspDevices, String>>()
        val request = mobileRepository.findTopByOrderByDateTimeDesc()
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "No requests found"))
        request.newRequiredState.split(",").forEach { state ->
            val list = state.split(":").map { it.trim() }
            DevTypes.values().forEach { devType ->
                if (devType.name == list[0]) {
                    val raspDev = raspDevicesRepository.findByDevType(devType)
                    if (raspDev != null) {
                        statePairs.add(MutablePair(raspDev, list[1]))
                        if (raspDev.devType == DevTypes.TEMP_SENSOR)
                            needTemp = list[1].toInt()
                    } else
                        return ResponseEntity.badRequest()
                            .body(MessageResponse(message = "Device ${devType.name} not found"))
                }
            }
        }
        if (statePairs.isEmpty())
            return ResponseEntity.badRequest()
                .body(MessageResponse(message = "No devices found"))

        val prevState = raspStateRepository.findTopByOrderByDateTimeDesc()?.raspState?.split(", ")


        prevState?.find { it.split(":")[0] == DevTypes.TEMP_SENSOR.toString() }?.let { prevTemp ->
            realTemp = prevTemp.split(":")[1].toInt()
        }
        var isCondWork = false
        statePairs.find { it.left.devType == DevTypes.CONDITIONER }?.let { cond ->
            prevState?.find { it.split(":")[0] == DevTypes.CONDITIONER.toString() }?.let { prevCond ->
                val isTurnOn = prevCond.split(":")[1].toBoolean()
                if (!cond.right.toBoolean()) {
                    cond.right = isTurnOn.toString()
                    if (realTemp <= (Config.turnOffCondDelta + needTemp))
                        cond.right = false.toString()
                    else if (realTemp >= (Config.turnOnCondDelta + needTemp)) {
                        cond.right = true.toString()
                        isCondWork = true
                    }
                }
            }
        }

        statePairs.find { it.left.devType == DevTypes.FAN }?.let { fan ->
            prevState?.find { it.split(":")[0] == DevTypes.FAN.toString() }?.let { prevFan ->
                val isTurnOn = prevFan.split(":")[1].toBoolean()
                if (!fan.right.toBoolean()) {
                    if (isCondWork)
                        fan.right = false.toString()
                    else {
                        fan.right = isTurnOn.toString()
                        if (realTemp <= (Config.turnOffFanDelta + needTemp))
                            fan.right = false.toString()
                        else if (realTemp >= (Config.turnOnFanDelta + needTemp)) {
                            fan.right = true.toString()
                            statePairs.find { it.left.devType == DevTypes.CONDITIONER }?.let { cond ->
                                cond.right = false.toString()
                            }
                        }
                    }
                }
            }
        }
        var finalState = ""
        statePairs.forEach { pair ->
            if (pair.left.devType == DevTypes.FAN
                || pair.left.devType == DevTypes.CONDITIONER
            )
                finalState += "${pair.left.pinId}:${pair.right}, "
        }
        finalState = finalState.dropLast(2)
        return ResponseEntity.ok(finalState)
    }
}