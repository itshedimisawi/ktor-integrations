package com.example.data.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PasswordRecoveryTemplateData(
    @SerialName("username")
    val username: String,
    @SerialName("reset_url")
    val resetUrl: String
)