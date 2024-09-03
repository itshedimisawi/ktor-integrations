package com.example.services

import com.example.data.dtos.sns.SNSNotification
import com.example.data.dtos.sns.SNSSubscribeNotification
import com.example.data.dtos.sns.ses_bounce.SNSBounceMessage
import com.example.data.dtos.sns.ses_complaint.SNSComplaintMessage
import kotlinx.serialization.json.Json
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.ConfirmSubscriptionRequest

class SnsServiceImpl(
    private val snsClient: SnsClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : SnsService {
    override suspend fun confirmSubscription(response: String, topicArn: String) {
        val token = json.decodeFromString<SNSSubscribeNotification>(response).token
        snsClient.confirmSubscription(
            ConfirmSubscriptionRequest.builder()
                .token(token)
                .topicArn(topicArn)
                .build()
        )
    }

    override suspend fun decodeBounceMessage(response: String): SNSBounceMessage {
        val content = json.decodeFromString<SNSNotification>(response)
        return json.decodeFromString<SNSBounceMessage>(content.message)
    }

    override suspend fun decodeComplaintMessage(response: String): SNSComplaintMessage {
        val content = json.decodeFromString<SNSNotification>(response)
        return json.decodeFromString<SNSComplaintMessage>(content.message)
    }

    override suspend fun decodeUnsubscribeMessage(response: String): Unit {
        TODO("Not yet implemented")
    }
}