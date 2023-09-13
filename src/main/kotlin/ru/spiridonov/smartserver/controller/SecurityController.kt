package ru.spiridonov.smartserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.spiridonov.smartserver.model.RaspState
import ru.spiridonov.smartserver.model.Security
import ru.spiridonov.smartserver.payload.request.SecurityRequest
import ru.spiridonov.smartserver.payload.response.MessageResponse
import ru.spiridonov.smartserver.repository.RaspStateRepository
import ru.spiridonov.smartserver.repository.SecurityRepository
import ru.spiridonov.smartserver.repository.UserRepository
import ru.spiridonov.smartserver.service.UserDetailsImpl
import java.time.OffsetDateTime
import java.time.ZoneOffset

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/security")
class SecurityController(
    val raspStateRepository: RaspStateRepository,
    val securityRepository: SecurityRepository,
    val userRepository: UserRepository
) {

    @PostMapping
    fun postSecurity(@Valid @RequestBody request: SecurityRequest): ResponseEntity<*> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        val user = userRepository.findByEmail(userDetails.getEmail())
            ?: return ResponseEntity.badRequest().body(MessageResponse(message = "User not found"))

        val securityState = Security(
            dateTime = OffsetDateTime.now(ZoneOffset.of("+03:00")),
            user = user,
            isSecurityTurnOn = request.newRequiredState
        )
        val savedState = securityRepository.save(securityState)

        raspStateRepository.findTopByOrderByDateTimeDesc()?.copy(
            dateTime = OffsetDateTime.now(ZoneOffset.of("+03:00")),
            isSecurityViolated = false
        )?.let { state ->
            raspStateRepository.save(state)
        }

        return ResponseEntity.ok(savedState)
    }

    @PostMapping("/violated")
    fun postSecurityViolated(): ResponseEntity<*> =
        raspStateRepository.findTopByOrderByDateTimeDesc()?.copy(
            dateTime = OffsetDateTime.now(ZoneOffset.of("+03:00")),
            isSecurityViolated = true
        )
            .let { state ->
                val isSecurityTurnOn = securityRepository.findTopByOrderByDateTimeDesc()?.isSecurityTurnOn ?: true
                if (!isSecurityTurnOn) return ResponseEntity.ok(MessageResponse("Security is turned off"))
                val savedState = if (state != null)
                    raspStateRepository.save(state)
                else raspStateRepository.save(
                    RaspState(
                        dateTime = OffsetDateTime.now(ZoneOffset.of("+03:00")),
                        raspState = "",
                        isSecurityViolated = true
                    )
                )
                return ResponseEntity.ok(savedState)
            }

    @GetMapping("/violated_history")
    fun getViolatedHistory(): ResponseEntity<List<RaspState>> {
        val violatedHistory = raspStateRepository.findAllByIsSecurityViolatedTrueOrderByDateTimeDesc()
        return ResponseEntity.ok(violatedHistory)
    }

    @GetMapping("/get")
    fun getSecurity(): ResponseEntity<*> {
        val securityState = securityRepository.findTopByOrderByDateTimeDesc()
        return if (securityState != null)
            ResponseEntity.ok(securityState)
        else
            ResponseEntity.badRequest().body(MessageResponse("Security state not found"))
    }
}