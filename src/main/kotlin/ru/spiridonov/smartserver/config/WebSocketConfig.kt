package ru.spiridonov.smartserver.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import ru.spiridonov.smartserver.controller.SecurityWebSocket
import ru.spiridonov.smartserver.repository.RaspStateRepository
import ru.spiridonov.smartserver.repository.SecurityRepository


@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    @Autowired
    private lateinit var raspStateRepository: RaspStateRepository

    @Autowired
    private lateinit var securityRepository: SecurityRepository

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(
            SecurityWebSocket(
                raspStateRepository,
                securityRepository
            ), "/security_websocket"
        )
    }
}