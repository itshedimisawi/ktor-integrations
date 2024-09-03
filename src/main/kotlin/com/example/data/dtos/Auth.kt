package com.example.data.dtos

import com.example.data.models.user.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val token: String? = null, val user: User? = null, val errors: List<FieldError> = listOf())

@Serializable
data class LoginBody(val email: String, val password: String)

@Serializable
data class SignupBody(val email: String, val fullName: String, val username: String, val password: String)



@Serializable
data class RecoverPasswordBody (
    val email: String,
)

@Serializable
data class CheckRecoveryCodeBody (
    val email:String,
    val code:String
)

@Serializable
data class ResetPasswordBody (
    val code:String,
    val newPassword:String
)

