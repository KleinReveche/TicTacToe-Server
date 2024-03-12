package me.kleinreveche

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import me.kleinreveche.tictactoe.GameRoomManager
import me.kleinreveche.tictactoe.data.models.PlayerMove
import me.kleinreveche.tictactoe.gameSocket

fun Application.configureRouting() {
    val gameRoomManager = GameRoomManager()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("/play/{roomId}") {
            gameSocket(gameRoomManager)
        }

        post("/get_room") {
            val roomId = gameRoomManager.getOrCreateRoom()
            call.respondText(roomId)
        }
    }
}

fun extractAction(message: String): Pair<String, Any> {
    // deviceToken$action_type#{...}
    val deviceToken = message.substringBefore("|")
    val type = message.substringBefore("#").substringAfter("|")
    val payload = message.substringAfter("#")

    return when (type) {
        "player_move" -> { Pair(deviceToken, Json.decodeFromString<PlayerMove>(payload)) }
        else -> throw IllegalArgumentException("Unknown message type")
    }
}
