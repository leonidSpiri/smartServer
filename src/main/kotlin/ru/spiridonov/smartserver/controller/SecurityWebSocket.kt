package ru.spiridonov.smartserver.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.spiridonov.smartserver.repository.RaspStateRepository
import ru.spiridonov.smartserver.repository.SecurityRepository
import java.util.concurrent.atomic.AtomicLong

class SecurityWebSocket(
    private val raspStateRepository: RaspStateRepository,
    private val securityRepository: SecurityRepository
) :
    TextWebSocketHandler() {

    init {
        checkAndSend()
    }

    private val sessionList = HashMap<WebSocketSession, Long>()
    private var uids = AtomicLong(0)

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList -= session
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)
        sessionList[session] = uids.getAndIncrement()
    }

    private fun checkAndSend() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (sessionList.isNotEmpty()) {
                    val isSecurityTurnOn = securityRepository.findTopByOrderByDateTimeDesc()?.isSecurityTurnOn ?: true
                    val state = raspStateRepository.findTopByOrderByDateTimeDesc()
                    val newState = if (!isSecurityTurnOn) state?.copy(isSecurityViolated = false) else state
                    if (newState?.isSecurityViolated == true)
                        sessionList.forEach { emit(it.key) }
                    delay(5000)
                } else delay(30000)
            }
        }
    }

    private fun emit(session: WebSocketSession) =
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString("violated")))
}