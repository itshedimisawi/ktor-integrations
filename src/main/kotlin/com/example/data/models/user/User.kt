package com.example.data.models.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val id: Int,
    val email:String,
    val fullName: String,
    val username: String
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 128).uniqueIndex()
    val email = varchar("email", 128).uniqueIndex()
    val fullName = varchar("full_name", 128)
    val password = varchar("password", 1024)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}