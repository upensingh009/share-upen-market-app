package com.example.shareupenmarket.market

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.shareupenmarket.notifications.TradeNotifier

class MarketAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notifier = TradeNotifier(context)
        notifier.showAlert("Market Closed", "The Indian market has closed (15:30 IST).")
        // In a production implementation, reschedule next day's alarm here.
    }
}
