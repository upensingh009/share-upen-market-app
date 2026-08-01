package com.example.shareupenmarket.market

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

object MarketHours {
    private val zone = ZoneId.of("Asia/Kolkata")
    private val marketOpen = LocalTime.of(9, 15)
    private val marketClose = LocalTime.of(15, 30)

    fun isWithinMarketHours(epochMillis: Long = Instant.now().toEpochMilli()): Boolean {
        val now = Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalTime()
        return !now.isBefore(marketOpen) && !now.isAfter(marketClose)
    }
}
