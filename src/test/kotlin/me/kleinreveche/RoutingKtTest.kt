package me.kleinreveche

import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingKtTest {

    @Test
    fun testPostPlay() = testApplication {
        application {
            module()
        }
        client.post("/play").apply {
            assertEquals(HttpStatusCode.OK, status)

            val roomId = bodyAsText()
            println("Room ID: $roomId")

            assertEquals(6, roomId.length)
        }
    }
}