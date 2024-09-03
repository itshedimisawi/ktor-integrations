package com.example.utils

import java.time.Instant
import java.util.*


fun String.isValidEmail() =
    !isNullOrEmpty() && "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex().matches(this)

fun String.isStrongPassword() = length >= 9

fun generateAuthToken(): String {
    return (1..100)
        .map { kotlin.random.Random.nextInt(0, 26) }
        .map { i -> ('a'..'z').toList()[i] }
        .joinToString("")
}

fun generateRandomFilename() = "${Instant.now().toEpochMilli()}_${UUID.randomUUID().toString().take(13).filter { it != '-' }}"