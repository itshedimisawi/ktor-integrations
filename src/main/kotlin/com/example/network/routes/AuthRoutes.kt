package com.example.network.routes

import com.example.data.dtos.*
import com.example.data.repositories.AuthenticationRepo
import com.example.utils.isStrongPassword
import com.example.utils.isValidEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.AuthRoutes() {
    val authDao by inject<AuthenticationRepo>()

    // post: LoginBody -> AuthResponse
    post("/auth/login") {
        val user = call.receive<LoginBody>()

        val errors = ArrayList<FieldError>()
            .apply {
                if (user.email.isBlank()) {
                    add(FieldError("username", "Email is required"))
                }
                if (user.password.isBlank()) {
                    add(FieldError("password", "Password is required"))
                }
            }

        if (errors.isNotEmpty()) {
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(errors = errors)
            )
            return@post
        }
        val result = authDao.login(email = user.email, password = user.password)
        if (result == null) {
            call.respond(
                HttpStatusCode.OK, AuthResponse(
                    errors = listOf(
                        FieldError(field = "password", error = "Email or password is incorrect")
                    )
                )
            )
            return@post
        }
        call.respond(
            HttpStatusCode.OK, AuthResponse(token = result.token, user = result.user),
        )
    }

    // post: SignupBody -> AuthResponse
    post("/auth/signup") {
        val user = call.receive<SignupBody>()
        val errors = ArrayList<FieldError>()
            .apply {
                if (user.email.isBlank()) {
                    add(FieldError("email", "Email is required"))
                }
                if (user.fullName.isBlank()) {
                    add(FieldError("fullName", "Full name is required"))
                }
                if (user.username.isBlank()) {
                    add(FieldError("username", "Username is required"))
                }
                if (user.password.isBlank()) {
                    add(FieldError("password", "Password is required"))
                }
            }

        if (errors.isNotEmpty()) {
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(errors = errors)
            )
            return@post
        }
        val result = authDao.signup(
            email = user.email,
            fullName = user.fullName,
            username = user.username,
            password = user.password
        )
        if (result == null) {
            call.respond(
                HttpStatusCode.OK, AuthResponse(
                    errors = listOf(
                        FieldError(field = "email", error = "Email already exists")
                    )
                )
            )
            return@post
        }
        call.respond(
            HttpStatusCode.OK, AuthResponse(token = result.token, user = result.user),
        )
    }
}

fun Route.PasswordRecoveryRoutes() {
    val authDao by inject<AuthenticationRepo>()

    // post: RecoverPasswordBody -> GenericResponse
    post("/auth/recover_password/recover") {
        val body = call.receive<RecoverPasswordBody>()
        if (!body.email.isValidEmail()) {
            call.respond(
                HttpStatusCode.BadRequest,
                GenericResponse(errors = listOf(FieldError("email", "Invalid email")))
            )
            return@post
        }
        authDao.createPasswordRecovery(
            email = body.email
        )
        call.respond(
            HttpStatusCode.Created,
            GenericResponse("Recovery code sent to your email")
        )
    }

    // post: CheckRecoveryCodeBody -> GenericResponse
    post("/auth/recover_password/check_code") {
        val body = call.receive<CheckRecoveryCodeBody>()
        if (body.code.length != 36) { // UUID length
            call.respond(
                HttpStatusCode.BadRequest,
                GenericResponse("Invalid recovery code")
            )
            return@post
        }
        if (authDao.checkRecoveryCode(code = body.code, email = body.email)) {
            call.respond(
                HttpStatusCode.OK,
                GenericResponse("This code is valid")
            )
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                GenericResponse("This code is invalid or has expired")
            )
        }
    }

    // post: ResetPasswordBody -> GenericResponse
    post("/auth/recover_password/reset_password") {
        val body = call.receive<ResetPasswordBody>()

        if (body.code.length != 36) { // UUID length
            call.respond(
                HttpStatusCode.BadRequest,
                GenericResponse("Invalid recovery code")
            )
            return@post
        }

        if (!body.newPassword.isStrongPassword()) {
            call.respond(
                HttpStatusCode.OK,
                GenericResponse("Password is weak")
            )
            return@post
        }

        if (authDao.resetPassword(
                code = body.code,
                newPassword = body.newPassword,
            )
        ) {
            call.respond(
                HttpStatusCode.Created,
                GenericResponse("Password changed successfully")
            )
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
            )
        }
    }
}