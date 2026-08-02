package com.example.shareupenmarket

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shareupenmarket.auth.AngleOneAuthManager
import com.example.shareupenmarket.auth.LocalCredentialStore
import com.example.shareupenmarket.databinding.ActivityMainBinding
import com.example.shareupenmarket.market.MarketDataService
import com.example.shareupenmarket.market.MarketWorker
import com.example.shareupenmarket.notifications.TradeNotifier
import com.example.shareupenmarket.suggestion.SignalService
import com.example.shareupenmarket.trading.TradingEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authManager by lazy { AngleOneAuthManager(applicationContext) }
    private val credentialStore by lazy { LocalCredentialStore(applicationContext) }
    private val marketDataService = MarketDataService()
    private val signalService = SignalService()
    private val tradingEngine = TradingEngine()
    private val notifier by lazy { TradeNotifier(applicationContext) }
    private var refreshJob: Job? = null
    private var lastAlertAction: String? = null
    private var lastAlertTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.statusText.text = "Initializing local trading flow..."
        maybeRequestNotificationPermission()

        binding.btnRunWorker.setOnClickListener {
            val req = OneTimeWorkRequestBuilder<MarketWorker>().build()
            WorkManager.getInstance(applicationContext).enqueue(req)
        }

        refreshJob = lifecycleScope.launch {
            while (isActive) {
                val saved = credentialStore.load()
                if (saved == null) {
                    credentialStore.save(
                        com.example.shareupenmarket.auth.AngelCredentials(
                            apiKey = "demo-api-key",
                            clientId = "demo-client-id",
                            password = "demo-password",
                            totp = "123456",
                        )
                    )
                }

                val token = authManager.authenticate()
                val price = marketDataService.fetchLivePrice("NIFTY")
                val suggestion = signalService.getSuggestion(price)
                val decision = tradingEngine.evaluate(price, suggestion)

                val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                if (!isFinishing && !isDestroyed) {
                    binding.niftyPrice.text = "₹${String.format(Locale.US, "%.2f", price)}"
                    binding.sensexPrice.text = "₹${String.format(Locale.US, "%.2f", price + 1000)}"
                    binding.signalBadge.text = decision.action.uppercase()
                    binding.signalBadge.setBackgroundColor(
                        when (decision.action.uppercase()) {
                            "BUY" -> 0xFFE8F5E9.toInt()
                            "SELL" -> 0xFFFFEBEE.toInt()
                            else -> 0xFFFFF8E1.toInt()
                        }
                    )
                    binding.signalBadge.setTextColor(
                        when (decision.action.uppercase()) {
                            "BUY" -> 0xFF2E7D32.toInt()
                            "SELL" -> 0xFFC62828.toInt()
                            else -> 0xFFEF6C00.toInt()
                        }
                    )
                    binding.lastUpdatedText.text = "Last update: $timeStamp"
                    val targetText = decision.target?.let { "Target: ₹${String.format(Locale.US, "%.2f", it)}" } ?: "Target: --"
                    val stopLossText = decision.stopLoss?.let { "Stop-Loss: ₹${String.format(Locale.US, "%.2f", it)}" } ?: "Stop-Loss: --"
                    val riskText = "Risk Score: ${decision.riskScore}/100"
                    val tradeText = if (decision.shouldTrade) "Trade Allowed: yes" else "Trade Allowed: no"
                    binding.statusText.text = "Auth: ${if (token != null) "ok" else "failed"}\nSignal: ${suggestion.reason}\nDecision: ${decision.action}\n${decision.message}\n$targetText\n$stopLo[...]"
                }

                val action = decision.action.uppercase()
                val now = System.currentTimeMillis()
                val shouldAlert = (action == "BUY" || action == "SELL") &&
                    (lastAlertAction != action || now - lastAlertTime > 60_000)

                if (shouldAlert) {
                    notifier.showAlert(
                        title = "Trade Signal $action",
                        body = "${decision.message}\nTarget: ${decision.target?.let { "₹${String.format(Locale.US, "%.2f", it)}" } ?: "--"}\nStop-Loss: ${decision.stopLoss?.let { "₹${String.f[...] }}"
                    )
                    lastAlertAction = action
                    lastAlertTime = now
                }
                delay(15_000)
            }
        }
    }

    override fun onDestroy() {
        refreshJob?.cancel()
        super.onDestroy()
    }

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
    }
}
