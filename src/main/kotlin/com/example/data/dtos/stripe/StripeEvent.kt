package com.imprint.models.misc.stripe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// This is a very minimal event object, just to show the concept
// You can find the full object here: https://docs.stripe.com/api/setup_intents/object
@Serializable
data class StripeEvent(
    @SerialName("object")
    val objectX: StripeEventObject?
)