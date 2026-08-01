package com.example.shareupenmarket

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shareupenmarket.market.MarketWorker
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class ShareUpenMarketApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // Enqueue periodic WorkManager job to poll market data every 15 minutes
        val work = PeriodicWorkRequestBuilder<MarketWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "market_poll_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )

        scheduleMarketCloseAlarm()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "trade_alerts",
                "Trade Alerts",
                android.app.NotificationManager.IMPORTANCE_HIGH,
            )
            channel.description = "Notifications for strong intraday trade signals"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun scheduleMarketCloseAlarm() {
        try {
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, com.example.shareupenmarket.market.MarketAlarmReceiver::class.java)
            val pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 15)
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_MONTH, 1)
            }

            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
        } catch (e: Exception) {
            // Fail silently — alarm scheduling may not be available on some devices/hosts
        }
    }
}
