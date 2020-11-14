package com.mazenk.todoist.integration

import com.mazenk.todoist.GlobalContext
import com.mazenk.todoist.data.AuthenticationHandler
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlin.properties.Delegates

class TodoistServer {
    var server: ApplicationEngine by Delegates.notNull()

    fun start() {
        server = embeddedServer(Netty, 80) {
            routing {
                get("authorize") {
                    val code = call.request.queryParameters["code"]
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing code")
                    val state = call.request.queryParameters["state"]
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing state")

                    val telegramId = AuthenticationHandler.findTelegramUser(state)
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid state")

                    val accessToken = TodoistIntegration.receiveAccessToken(code)

                    AuthenticationHandler.handleAuthToken(state, accessToken, telegramId)
                    call.respondRedirect("https://telegram.me/${GlobalContext.botUsername}", false)
                }
            }
        }

        server.start(wait = false)
    }
}