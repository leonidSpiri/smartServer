package ru.spiridonov.smartserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.atomic.AtomicLong


class User2(val id: Long, val name: String)
class Message(val msgType: String, val data: Any)
class ChatHandler : TextWebSocketHandler() {

    init {
        checkAndSend()
    }
    val sessionList = HashMap<WebSocketSession, User2>()
    var uids = AtomicLong(0)

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList -= session
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        super.handleTextMessage(session, message)
        val json = ObjectMapper().readTree(message.payload)
        // {type: "join/say", data: "name/msg"}

        when (json.get("type").asText()) {
            "join" -> {
                val user = User2(uids.getAndIncrement(), json.get("data").asText())
                sessionList.put(session, user)
                // tell this user about all other users
                emit(session, Message("users", sessionList.values))
                // tell all other users, about this user
                broadcastToOthers(session, Message("join", user))
            }

            "say" -> {
                broadcast(Message("say", json.get("data").asText()))
            }
        }
    }

    fun checkAndSend() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(5000)
                broadcast(Message("say", "autoupdate"))
            }
        }
    }

    fun emit(session: WebSocketSession, msg: Message) =
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))

    fun broadcast(msg: Message) = sessionList.forEach { emit(it.key, msg) }
    fun broadcastToOthers(me: WebSocketSession, msg: Message) =
        sessionList.filterNot { it.key == me }.forEach { emit(it.key, msg) }

}