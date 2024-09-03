package com.example.data.dtos.sns.ses_complaint


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Complaint(
    @SerialName("complainedRecipients")
    val complainedRecipients: List<ComplainedRecipient>
)