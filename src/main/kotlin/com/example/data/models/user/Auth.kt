package com.example.data.models.user

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


data class AuthResult(val token: String, val user: User)

object AuthTokens : Table() {
    val userId = integer("user_id").references(Users.id , onDelete = ReferenceOption.CASCADE)
    val token = varchar("token", 1024).uniqueIndex()
    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}
