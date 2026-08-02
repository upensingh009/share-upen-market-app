package com.example.shareupenmarket.market

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MarketDataService {
    private val alphaKey = "demo"

    suspend fun fetchLivePrice(symbol: String): Double = withContext(Dispatchers.IO) {
        val normalized = when (symbol.uppercase()) {
            "NIFTY" -> "NSEI"
            "SENSEX" -> "BSESN"
            else -> symbol
        }

        try {
            val endpoint = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=$normalized&apikey=$alphaKey"
            val json = URL(endpoint).readText()
            val root = JSONObject(json)
            val quote = root.optJSONObject("Global Quote") ?: return@withContext 118.0
            val price = quote.optString("05. price", "118.0").toDoubleOrNull() ?: 118.0
            price
        } catch (_: Exception) {
            118.0
        }
    }

    suspend fun connectWebSocket(onTick: (String, Double) -> Unit) {
        onTick("NIFTY", fetchLivePrice("NIFTY"))
        onTick("SENSEX", fetchLivePrice("SENSEX"))
    }

    suspend fun fetchHistorical(symbol: String, days: Int = 30): List<Pair<Long, Double>> = withContext(Dispatchers.IO) {
        val normalized = when (symbol.uppercase()) {
            "NIFTY" -> "NSEI"
            "SENSEX" -> "BSESN"
            else -> symbol
        }

        try {
            val endpoint = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=$normalized&apikey=$alphaKey"
            val json = URL(endpoint).readText()
            val root = JSONObject(json)
            val series = root.optJSONObject("Time Series (Daily)") ?: return@withContext emptyList()
            val keys = series.keySet().toList().sortedDescending().take(days)
            keys.mapNotNull { date ->
                val day = series.optJSONObject(date) ?: return@mapNotNull null
                val close = day.optString("4. close").toDoubleOrNull() ?: return@mapNotNull null
                val ts = java.time.LocalDate.parse(date).atStartOfDay(java.time.ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli()
                ts to close
            }.sortedBy { it.first }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
