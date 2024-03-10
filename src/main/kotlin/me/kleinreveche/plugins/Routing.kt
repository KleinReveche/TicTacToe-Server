package me.kleinreveche.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.kleinreveche.models.SingleTicTacToeGame
import me.kleinreveche.singleGameRoom

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        val game = SingleTicTacToeGame()
        singleGameRoom(game)
    }
}
