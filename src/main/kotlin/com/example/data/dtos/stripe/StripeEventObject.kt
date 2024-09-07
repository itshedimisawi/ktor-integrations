package com.imprint.models.misc.stripe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StripeEventObject(
    @SerialName("id")
    val id: String?,
    @SerialName("object")
    val objectX: String?,
    @SerialName("amount")
    val amount: Int?,
    @SerialName("amount_capturable")
    val amountCapturable: Int?,
    @SerialName("amount_received")
    val amountReceived: Int?,
    @SerialName("capture_method")
    val captureMethod: String?,
    @SerialName("client_secret")
    val clientSecret: String?,
    @SerialName("confirmation_method")
    val confirmationMethod: String?,
    @SerialName("created")
    val created: Int?,
    @SerialName("currency")
    val currency: String?,
    @SerialName("customer")
    val customer: String?
)