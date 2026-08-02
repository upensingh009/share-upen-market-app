package com.example.shareupenmarket.market

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shareupenmarket.notifications.TradeNotifier
import com.example.shareupenmarket.trading.TradingEngine
import com.example.shareupenmarket.suggestion.SignalService

class MarketWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    private val marketService = MarketDataService()
    private val signalService = SignalService()
    private val tradingEngine = TradingEngine()
    private val notifier = TradeNotifier(appContext)

    override suspend fun doWork(): Result {
        return try {
            val price = marketService.fetchLivePrice("NIFTY")
            val suggestion = signalService.getSuggestion(price)
            val decision = tradingEngine.evaluate(price, suggestion)

            if (MarketHours.isWithinMarketHours()) {
                if (decision.shouldTrade && (decision.action == "BUY" || decision.action == "SELL")) {
                    notifier.showAlert("Trade Signal ${decision.action}", decision.message)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
