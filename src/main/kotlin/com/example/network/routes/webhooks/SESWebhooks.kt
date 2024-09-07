package com.example.network.routes.webhooks

import com.example.constants.SNS_MESSAGE_TYPE_HEADER
import com.example.constants.SNS_MESSAGE_TYPE_NOTIFICATION
import com.example.constants.SNS_MESSAGE_TYPE_SUBSCRIPTION_CONFIRMATION
import com.example.data.dtos.sns.SNSResponse
import com.example.services.SnsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.SesWebhooks() {
    val snsService by inject<SnsService>()
    val isDev = environment!!.config.property("project.isDev").getString().toBoolean()
    val topicARNBounce = environment!!.config.property("${if (isDev) "dev" else "prod"}.sns.topic_arn_bounce").getString()
    val topicARNComplaint = environment!!.config.property("${if (isDev) "dev" else "prod"}.sns.topic_arn_complaint").getString()
    val topicARUnsubscribe = environment!!.config.property("${if (isDev) "dev" else "prod"}.sns.topic_arn_unsubscribe").getString()

    post("/webhooks/sns/handle-bounces") {
        val header = call.request.headers[SNS_MESSAGE_TYPE_HEADER]
        when (header) {
            SNS_MESSAGE_TYPE_SUBSCRIPTION_CONFIRMATION -> {
                val body = call.receiveText()
                snsService.confirmSubscription(response = body, topicArn = topicARNBounce)
                call.respond(HttpStatusCode.OK, SNSResponse(true, "Subscription confirmed"))
            }

            SNS_MESSAGE_TYPE_NOTIFICATION -> {
                val body = call.receiveText()
                snsService.decodeBounceMessage(response = body).bounce.bouncedRecipients.forEach { recipient ->
                    // Do something with the recipient email when it bounces
                    println("Bounce: ${recipient.emailAddress}")
                }
                call.respond(HttpStatusCode.OK, SNSResponse(true, "Bounce handled"))
            }

            else -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SNSResponse(false, "Unknown header: $header")
                )
            }
        }
    }

    post("/webhooks/sns/handle-complaints") {
        val header = call.request.headers[SNS_MESSAGE_TYPE_HEADER]
        when (header) {
            SNS_MESSAGE_TYPE_SUBSCRIPTION_CONFIRMATION -> {
                val body = call.receiveText()
                snsService.confirmSubscription(body, topicARNComplaint)
                call.respond(HttpStatusCode.OK, SNSResponse(true, "Subscription confirmed"))
            }

            SNS_MESSAGE_TYPE_NOTIFICATION -> {
                val body = call.receiveText()
                snsService.decodeComplaintMessage(response = body).complaint.complainedRecipients.forEach { recipient ->
                    // Do something with the recipient email when a complaint is received
                    println("Complaint: ${recipient.emailAddress}")
                }
                call.respond(HttpStatusCode.OK, SNSResponse(true, "Complaint received"))
            }

            else -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SNSResponse(false, "Unknown header: $header")
                )
            }
        }
    }

    post("/webhooks/sns/unsubscribe") {
        val header = call.request.headers[SNS_MESSAGE_TYPE_HEADER]
        when (header) {
            SNS_MESSAGE_TYPE_SUBSCRIPTION_CONFIRMATION -> {
                val body = call.receiveText()
                snsService.confirmSubscription(body, topicARUnsubscribe)
                call.respond(HttpStatusCode.OK, SNSResponse(true, "Subscription confirmed"))
            }

            SNS_MESSAGE_TYPE_NOTIFICATION-> {
                //val body = call.receiveText()
                // Do something with the notification when a user unsubscribes

                call.respond(HttpStatusCode.OK, SNSResponse(true, "Notification received"))
            }

            else -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SNSResponse(false, "Unknown header: $header")
                )
            }
        }
    }
}