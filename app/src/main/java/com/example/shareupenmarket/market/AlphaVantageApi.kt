package com.example.shareupenmarket.market

import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {
    @GET("query")
    suspend fun getTimeSeries(
        @Query("function") function: String = "TIME_SERIES_INTRADAY",
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "1min",
        @Query("apikey") apiKey: String,
    ): Any
}
