package me.kleinreveche

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import me.kleinreveche.models.PlayerMove
import me.kleinreveche.models.SingleTicTacToeGame

fun Route.singleGameRoom(game: SingleTicTacToeGame) {
    route("/play-single-room") {
        webSocket {
            val player = game.connectPlayer(this)

            if (player == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "2 players already connected"))
                return@webSocket
            }

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val message = frame.readText()
                        when (val action = extractAction(message)) {
                            is PlayerMove -> {
                                game.handleMove(player, action.move, action.gameVersion)
                            }
                            "get_player_char" -> {
                                outgoing.send(Frame.Text(player.toString()))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                game.disconnectPlayer(player)
            }
        }
    }
}

private fun extractAction(message: String): Any {
    // player_move#{...}
    val type = message.substringBefore("#")
    val payload = message.substringAfter("#")

    return when (type) {
        "player_move" -> {
            Json.decodeFromString(payload)
        }
        "get_player_char" -> {
            type
        }
        else -> throw IllegalArgumentException("Unknown message type")
    }
}