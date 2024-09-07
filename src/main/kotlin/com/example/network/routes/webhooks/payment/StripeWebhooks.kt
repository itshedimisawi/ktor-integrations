package com.example.network.routes.webhooks.payment

import com.example.constants.STRIPE_EVENT_TYPE_PAYMENT_INTENT_FAILED
import com.example.constants.STRIPE_EVENT_TYPE_PAYMENT_INTENT_SUCCEEDED
import com.example.constants.STRIPE_HOOK_SIGNATURE_HEADER
import com.imprint.models.misc.stripe.StripeEvent
import com.stripe.net.Webhook
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json


// You can test this webhook in developement mode using the Stripe CLI
// stripe listen --forward-to localhost:9090/webhooks/stripe
// Or using the Stripe dashboard https://dashboard.stripe.com/test/webhooks

fun Route.StripeWebhooks() {
    val json = Json {
        ignoreUnknownKeys = true
    }
    val isDev = environment!!.config.property("project.isDev").getString().toBoolean()
    val webhookSecret = environment!!.config.property("stripe.${if (isDev) "dev" else "prod"}.webhook_secret").getString()

    post("/webhooks/stripe") {
        val payload = call.receiveText()
        val sigHeader = call.request.headers[STRIPE_HOOK_SIGNATURE_HEADER]

        if (sigHeader != null) {
            try {
                val event = Webhook.constructEvent(
                    payload, sigHeader, webhookSecret
                )

                when (event.type) {
                    STRIPE_EVENT_TYPE_PAYMENT_INTENT_SUCCEEDED -> {
                        //  Example: fetch the payment intent id
                        val paymentIntentId = json.decodeFromString<StripeEvent>(event.data.toJson()).objectX!!.id!!
                        println("[stripe] payment succeeded: $paymentIntentId")
                    }
                    STRIPE_EVENT_TYPE_PAYMENT_INTENT_FAILED -> {
                        println("[stripe] payment failed")
                    }
                    else -> {
                        println("[stripe] Unhandled event type: ${event.type}")
                    }
                }
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "[stripe] Webhook error: ${e.localizedMessage}")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "[stripe] Missing $STRIPE_HOOK_SIGNATURE_HEADER header")
        }
    }
}