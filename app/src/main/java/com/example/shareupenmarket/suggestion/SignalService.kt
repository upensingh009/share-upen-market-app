package com.example.shareupenmarket.suggestion

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignalService {
    suspend fun getSuggestion(price: Double): SignalSuggestion = withContext(Dispatchers.IO) {
        val rsi = 68
        val macdSignal = "bullish"
        val shortMa = price - 3
        val longMa = price + 2
        val newsBias = getNewsBias()
        val reason = "RSI=$rsi, MACD=$macdSignal, shortMA=$shortMa, longMA=$longMa, news=$newsBias"
        SignalSuggestion(reason = reason, confidence = 74, newsBias = newsBias)
    }

    private fun getNewsBias(): String {
        return listOf("positive", "negative", "neutral").random()
    }
}
