package com.example.shareupenmarket.auth

import android.content.Context
import android.util.Base64
import java.util.UUID

class AngleOneAuthManager(context: Context) {
    private val credentialStore = LocalCredentialStore(context)

    suspend fun authenticate(): String? {
        val credentials = credentialStore.load() ?: return null
        return if (credentials.apiKey.isBlank() || credentials.clientId.isBlank() || credentials.password.isBlank() || credentials.totp.isBlank()) {
            null
        } else {
            "local-demo-token-${UUID.randomUUID()}"
        }
    }

    fun buildAuthorizationHeader(): String {
        val credentials = credentialStore.load() ?: return ""
        val payload = "${credentials.clientId}:${credentials.password}"
        return "Basic " + Base64.encodeToString(payload.toByteArray(), Base64.NO_WRAP)
    }
}
