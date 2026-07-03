package com.hermes.trading.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Minimal Bitget public endpoint for demo – account balance
interface BitgetApi {
    @GET("api/v5/account/balance")
    suspend fun getBalance(
        @Header("ACCESS-KEY") apiKey: String,
        @Header("ACCESS-SIGN") sign: String,
        @Header("ACCESS-TIMESTAMP") timestamp: String,
        @Header("ACCESS-PASSPHRASE") passphrase: String,
        @Query("coin") coin: String = "USDT"
    ): Response<BalanceResponse>
}

data class BalanceResponse(
    val code: String,
    val msg: String,
    val data: List<BalanceData>
)

data class BalanceData(
    val available: String,
    val hold: String,
    val coinId: String,
    val coin: String,
    val total: String,
    val timestamp: Long
)
