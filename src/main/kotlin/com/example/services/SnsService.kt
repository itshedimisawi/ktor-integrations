package com.example.services

import com.example.data.dtos.sns.ses_bounce.SNSBounceMessage
import com.example.data.dtos.sns.ses_complaint.SNSComplaintMessage

interface SnsService {
    suspend fun confirmSubscription(response: String, topicArn: String)
    suspend fun decodeBounceMessage(response: String): SNSBounceMessage
    suspend fun decodeComplaintMessage(response: String): SNSComplaintMessage
    suspend fun decodeUnsubscribeMessage(response: String): Unit
}