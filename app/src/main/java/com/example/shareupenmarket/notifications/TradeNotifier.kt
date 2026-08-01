package com.example.shareupenmarket.notifications

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.shareupenmarket.MainActivity
import com.example.shareupenmarket.R

class TradeNotifier(private val context: Context) {
    private val prefs = context.getSharedPreferences("trade_alerts_prefs", Context.MODE_PRIVATE)

    fun showAlert(title: String, body: String): Boolean {
        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        val action = extractAction(title, body)
        if (!shouldPostAlert(action)) return false

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, "trade_alerts")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
        return true
    }

    private fun extractAction(title: String, body: String): String? {
        val upper = (title + " " + body).uppercase()
        return when {
            " BUY" in upper || upper.endsWith("BUY") || upper.contains(" TRADE SIGNAL BUY") -> "BUY"
            " SELL" in upper || upper.endsWith("SELL") || upper.contains(" TRADE SIGNAL SELL") -> "SELL"
            else -> null
        }
    }

    private fun shouldPostAlert(action: String?, cooldownMs: Long = 60_000L): Boolean {
        if (action == null) return true // keep market-close and informational notifications
        val lastAction = prefs.getString("last_alert_action", null)
        val lastTime = prefs.getLong("last_alert_time", 0L)
        val now = System.currentTimeMillis()
        if (lastAction == action && now - lastTime < cooldownMs) return false
        prefs.edit().putString("last_alert_action", action).putLong("last_alert_time", now).apply()
        return true
    }
}
