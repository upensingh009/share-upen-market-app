package com.example.shareupenmarket.market

import android.content.Context
import android.content.Intent
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.Calendar
import java.util.TimeZone

class MarketAlarmReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notifier = com.example.shareupenmarket.notifications.TradeNotifier(context)
        notifier.showAlert("Market Closed", "The Indian market has closed (15:30 IST).")

        // Reschedule for next day at 15:30 IST
        try {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val rescheduleIntent = Intent(context, MarketAlarmReceiver::class.java)
            val pending = PendingIntent.getBroadcast(context, 0, rescheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 15)
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
            }
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
        } catch (_: Exception) {
            // ignore
        }
    }
}
