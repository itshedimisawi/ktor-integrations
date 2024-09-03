package com.example.data.dtos.sns.ses_complaint


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComplainedRecipient(
    @SerialName("emailAddress")
    val emailAddress: String
)