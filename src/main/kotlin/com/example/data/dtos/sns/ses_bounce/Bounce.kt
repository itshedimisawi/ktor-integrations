package com.example.data.dtos.sns.ses_bounce


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bounce(
    @SerialName("bouncedRecipients")
    val bouncedRecipients: List<BouncedRecipient>
)