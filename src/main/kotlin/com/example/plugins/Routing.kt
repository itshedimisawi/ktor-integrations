package com.example.plugins

import com.example.network.routes.AuthRoutes
import com.example.network.routes.PasswordRecoveryRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        AuthRoutes()
        PasswordRecoveryRoutes()
    }
}
