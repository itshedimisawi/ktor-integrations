package com.example.data.dtos

import kotlinx.serialization.Serializable


@Serializable
data class FieldError(
    val field:String,
    val error:String
)

@Serializable
data class GenericResponse(
    val message:String?=null,
    var errors: List<FieldError>? = null, // fields in which errors occurred, this can be used in forms
)