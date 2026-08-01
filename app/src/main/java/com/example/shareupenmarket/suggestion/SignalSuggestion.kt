package com.example.shareupenmarket.suggestion

data class SignalSuggestion(
    val reason: String,
    val confidence: Int,
    val newsBias: String = "neutral",
)
