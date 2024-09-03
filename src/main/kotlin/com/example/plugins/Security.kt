package com.example.plugins

import com.example.data.repositories.AuthenticationRepo
import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.koin.ktor.ext.inject


fun Application.configureSecurity() {
    val authDao by inject<AuthenticationRepo>()

    install(Authentication) {
        bearer("auth-bearer") {
            realm = "Access to the '/' path"
            authenticate { tokenCredential ->
                authDao.validateToken(token = tokenCredential.token)?.let {
                    return@authenticate UserIdPrincipal(it.toString())
                }
                null
            }
        }
    }
}
