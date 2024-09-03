package com.example.data.repositories

import com.example.data.models.user.AuthResult


interface AuthenticationRepo {
    suspend fun login(email: String, password: String): AuthResult?
    suspend fun signup(email: String, fullName: String, username:String, password: String): AuthResult?
    suspend fun validateToken(token: String): Int? // returns userId if the token is valid

    suspend fun createPasswordRecovery(email: String): Boolean
    suspend fun checkRecoveryCode(code: String, email: String): Boolean
    suspend fun resetPassword(code: String, newPassword:String): Boolean
}