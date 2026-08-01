package com.example.shareupenmarket.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class LocalCredentialStore(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_trading_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun save(credentials: AngelCredentials) {
        prefs.edit()
            .putString("api_key", credentials.apiKey)
            .putString("client_id", credentials.clientId)
            .putString("password", credentials.password)
            .putString("totp", credentials.totp)
            .apply()
    }

    fun load(): AngelCredentials? {
        val apiKey = prefs.getString("api_key", null) ?: return null
        val clientId = prefs.getString("client_id", null) ?: return null
        val password = prefs.getString("password", null) ?: return null
        val totp = prefs.getString("totp", null) ?: return null
        return AngelCredentials(apiKey, clientId, password, totp)
    }
}
