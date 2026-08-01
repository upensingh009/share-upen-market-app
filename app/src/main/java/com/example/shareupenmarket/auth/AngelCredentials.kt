package com.example.shareupenmarket.auth

data class AngelCredentials(
    val apiKey: String,
    val clientId: String,
    val password: String,
    val totp: String,
)
