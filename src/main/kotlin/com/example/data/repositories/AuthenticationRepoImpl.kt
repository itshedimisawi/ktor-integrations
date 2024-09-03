package com.example.data.repositories

import com.example.data.dtos.PasswordRecoveryTemplateData
import com.example.data.models.user.*
import com.example.plugins.dbQuery
import com.example.services.SesService
import com.example.utils.generateAuthToken
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.*

class AuthenticationRepoImpl(
    private val dynamoDbClient: DynamoDbClient,
    private val sesService: SesService,
    private val passwordResetCodeTable: String,
    private val passwordResetCodeTableKey: String,
    private val passwordRecoveryTemplate: String,
    private val passwordResetBaseUrl: String,
) : AuthenticationRepo {
    override suspend fun login(email: String, password: String): AuthResult? = dbQuery {
        val userRow =
            Users.slice(Users.password, Users.id).select { Users.email.eq(email) and Users.password.isNotNull() }
                .singleOrNull()
                ?: return@dbQuery null // email not found

        if (!BCrypt.checkpw(password, userRow[Users.password])) return@dbQuery null // wrong password
        val token = getOrCreateUserToken(userId = userRow[Users.id])

        AuthResult(user = userRow.toUser(), token = token)
    }

    private fun getOrCreateUserToken(userId: Int): String {
        AuthTokens.select { AuthTokens.userId.eq(userId) }.singleOrNull()?.let {
            return it[AuthTokens.token]
        }
        val newToken = generateAuthToken()
        AuthTokens.insert {
            it[AuthTokens.userId] = userId
            it[AuthTokens.token] = newToken
        }
        return newToken
    }

    override suspend fun signup(email: String, fullName: String, username: String, password: String): AuthResult? =
        dbQuery {
            Users.insertIgnore {
                it[Users.email] = email
                it[Users.fullName] = fullName
                it[Users.username] = username
                it[Users.password] = BCrypt.hashpw(password, BCrypt.gensalt())
            }.resultedValues?.firstOrNull()?.toUser()?.let { user ->
                val token = getOrCreateUserToken(user.id)
                AuthResult(user = user, token = token)
            }
        }

    override suspend fun validateToken(token: String): Int? = dbQuery{
        AuthTokens.slice(AuthTokens.userId)
            .select { AuthTokens.token.eq(token) }.singleOrNull()?.getOrNull(AuthTokens.userId)
    }

    override suspend fun createPasswordRecovery(email: String): Boolean = dbQuery {
        val randomCode = UUID.randomUUID().toString()
        val username = Users.select { Users.email eq email }.singleOrNull()?.get(Users.username) ?: return@dbQuery false

        // insert the recovery code into DynamoDB
        insertPasswordRecoveryCode(
            email = email,
            code = randomCode
        )

        // send the email containing a URL to reset the code
        sesService.sendEmail(
            to = email,
            template = passwordRecoveryTemplate,
            templateData = Json.encodeToString(PasswordRecoveryTemplateData(
                username = username,
                resetUrl = "${passwordResetBaseUrl}?code=${randomCode}&email=${email}"
            ))
        )
        true
    }

    private fun insertPasswordRecoveryCode(
        email: String, code: String
    ): Boolean {
        val itemValues = mutableMapOf<String, AttributeValue>()
        itemValues[passwordResetCodeTableKey] = AttributeValue.builder().s(code).build()
        itemValues["email"] = AttributeValue.builder().s(email).build()
        itemValues["expiry"] = AttributeValue.builder().n(
            (System.currentTimeMillis().plus(1000 * 60 * 3) / 1000).toString()
        ).build()

        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .item(itemValues)
                .tableName(passwordResetCodeTable)
                .build()
        )
        return true
    }


    override suspend fun checkRecoveryCode(code: String, email: String): Boolean = dbQuery {
        dynamoDbClient.getItem(
            GetItemRequest.builder()
                .key(mapOf(passwordResetCodeTableKey to AttributeValue.builder().s(code).build()))
                .tableName(passwordResetCodeTable)
                .build()
        ).item()["email"]?.s() == email
    }

    override suspend fun resetPassword(code: String, newPassword: String): Boolean = dbQuery {
        val email = dynamoDbClient.getItem(
            GetItemRequest.builder()
                .key(mapOf(passwordResetCodeTableKey to AttributeValue.builder().s(code).build()))
                .tableName(passwordResetCodeTable)
                .build()
        ).item()["email"]?.s() ?: return@dbQuery false

        if (Users.update({ Users.email eq email }) {
                it[Users.password] = BCrypt.hashpw(newPassword, BCrypt.gensalt())
            } == 0) {
            return@dbQuery false // user not found
        }

        deletePasswordRecoveryCode(code)
        true
    }

    private fun deletePasswordRecoveryCode(code: String) {
        val itemValues = mutableMapOf<String, AttributeValue>()
        itemValues[passwordResetCodeTableKey] = AttributeValue.builder().s(code).build()

        dynamoDbClient.deleteItem(
            DeleteItemRequest.builder()
                .key(itemValues)
                .tableName(passwordResetCodeTable)
                .build()
        )
    }
}

fun ResultRow.toUser() = User(
    id = this[Users.id],
    email = this[Users.email],
    fullName = this[Users.fullName],
    username = this[Users.username]
)
