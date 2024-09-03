package com.example.data.dtos.sns.ses_bounce


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SNSBounceMessage(
    @SerialName("bounce")
    val bounce: Bounce
)