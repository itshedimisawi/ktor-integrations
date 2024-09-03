package com.example.data.dtos.sns

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SNSSubscribeNotification(
    @SerialName("Token")
    val token: String
)

@Serializable
data class SNSNotification(
    @SerialName("Message")
    val message: String
)


@Serializable
data class SNSResponse(val success: Boolean, val message: String)