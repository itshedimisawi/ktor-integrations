package com.example.plugins

import com.example.data.models.user.AuthTokens
import com.example.data.models.user.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureDatabase() {
    val isDev = environment.config.property("project.isDev").getString().toBoolean()
    val storageConfig = if (isDev) "aws.dev.rds" else "aws.prod.rds"
    Database.connect(
        createHikariDataSource(
            url = environment.config.property("${storageConfig}.jdbcURL").getString(),
            driver = environment.config.property("${storageConfig}.driverClassName").getString(),
            user = environment.config.property("${storageConfig}.user").getString(),
            pass = environment.config.property("${storageConfig}.password").getString(),
        )
    )
    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            Users,
            AuthTokens,
            // Add tables here
        )
        runBlocking {
            // Populate database here
        }
    }
}

private fun createHikariDataSource(
    url: String,
    driver: String,
    user: String,
    pass: String
) = HikariDataSource(HikariConfig().apply {
    username = user
    password = pass
    driverClassName = driver
    jdbcUrl = url
    maximumPoolSize = 3
    isAutoCommit = false
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    validate()
})

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
