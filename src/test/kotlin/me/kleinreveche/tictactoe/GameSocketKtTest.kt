package me.kleinreveche.tictactoe

import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.runBlocking
import me.kleinreveche.module
import kotlin.test.Test
import kotlin.test.assertEquals

class GameSocketKtTest {
    @Test
    fun testGameSocket() = testApplication {
        application {
            module()
        }

        runBlocking {
            val player1 = createClient { install(WebSockets) }
            val roomId = player1.post("/play").bodyAsText()
            println("Room ID: $roomId")
            assertEquals(6, roomId.length)

            player1.webSocket("/play/$roomId") {
                var message = ""
                incoming.receive().apply { if (this is Frame.Text) { message = readText() } }

                println("Player 1 received: $message")
                val initialMessage = "{\"board\":[null,null,null,null,null,null,null,null,null],\"connectedPlayers\":[\"X\"]}"
                assertEquals(initialMessage, message)
            }
        }
    }
}