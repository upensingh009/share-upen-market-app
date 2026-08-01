package com.example.shareupenmarket.trading

import com.example.shareupenmarket.suggestion.SignalSuggestion

class TradingEngine {
    fun evaluate(price: Double, suggestion: SignalSuggestion): TradeDecision {
        val rsi = extractRsi(suggestion.reason)
        val macd = suggestion.reason.contains("bullish", ignoreCase = true)
        val fastTrend = suggestion.reason.contains("shortMA", ignoreCase = true)
        val newsBias = suggestion.newsBias.lowercase()

        val action = when {
            rsi != null && rsi < 30 && macd && newsBias != "negative" -> "BUY"
            rsi != null && rsi > 70 && macd && newsBias != "positive" -> "SELL"
            rsi != null && rsi < 35 && fastTrend && newsBias != "negative" -> "BUY"
            rsi != null && rsi > 65 && fastTrend && newsBias != "positive" -> "SELL"
            newsBias == "positive" && price < 100 -> "BUY"
            newsBias == "negative" && price > 150 -> "SELL"
            price < 100 -> "BUY"
            price > 150 -> "SELL"
            else -> "HOLD"
        }

        val target = when (action) {
            "BUY" -> price + (price * 0.008)
            "SELL" -> price - (price * 0.008)
            else -> null
        }

        val stopLoss = when (action) {
            "BUY" -> price - (price * 0.004)
            "SELL" -> price + (price * 0.004)
            else -> null
        }

        val confidence = suggestion.confidence
        val qualityScore = confidence + when (newsBias) {
            "positive" -> 5
            "negative" -> -10
            else -> 0
        }

        val riskScore = when {
            newsBias == "negative" -> 80
            newsBias == "positive" -> 40
            else -> 60
        }

        val trendQuality = if (macd && fastTrend) 1 else 0
        val shouldTrade = when {
            action == "HOLD" -> false
            riskScore > 70 -> false
            qualityScore < 70 -> false
            trendQuality == 0 -> false
            else -> true
        }

        val positionSize = if (shouldTrade) 1000.0 else 0.0

        val message = when {
            !shouldTrade -> "Risk is too high or the setup is weak. Avoid the trade and wait for a cleaner entry."
            action == "BUY" -> "High-quality bullish setup: momentum and trend align. Consider a disciplined buy with defined risk."
            action == "SELL" -> "High-quality bearish setup: momentum and trend align. Consider a disciplined sell with defined risk."
            else -> "Range-bound setup: no strong edge. Wait for breakout or pullback confirmation."
        }

        return TradeDecision(
            action = if (shouldTrade) action else "HOLD",
            message = "$message (${suggestion.reason})",
            target = if (shouldTrade) target else null,
            stopLoss = if (shouldTrade) stopLoss else null,
            riskScore = riskScore,
            positionSize = positionSize,
            shouldTrade = shouldTrade,
        )
    }

    private fun extractRsi(reason: String): Int? {
        val regex = Regex("RSI=(\\d+)")
        val match = regex.find(reason) ?: return null
        return match.groupValues[1].toIntOrNull()
    }
}
