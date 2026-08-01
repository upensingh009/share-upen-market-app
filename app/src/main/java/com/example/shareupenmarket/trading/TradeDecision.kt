package com.example.shareupenmarket.trading

data class TradeDecision(
    val action: String,
    val message: String,
    val target: Double? = null,
    val stopLoss: Double? = null,
    val riskScore: Int = 0,
    val positionSize: Double = 0.0,
    val shouldTrade: Boolean = true,
)
