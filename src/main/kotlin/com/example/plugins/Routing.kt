package com.example.plugins

import com.example.network.routes.AuthRoutes
import com.example.network.routes.PasswordRecoveryRoutes
import com.example.network.routes.webhooks.SesWebhooks
import com.example.network.routes.webhooks.payment.StripeWebhooks
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Routes
        AuthRoutes()
        PasswordRecoveryRoutes()

        // Webhooks
        SesWebhooks()
        StripeWebhooks()
    }
}
