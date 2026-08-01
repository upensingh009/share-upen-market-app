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
}
