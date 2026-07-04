package com.hermes.trading.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Bitget private endpoints – account balance + open futures positions
interface BitgetApi {

    @GET("api/v5/account/balance")
    suspend fun getBalance(
        @Header("ACCESS-KEY") ak: String,
        @Header("ACCESS-SIGN") sign: String,
        @Header("ACCESS-TIMESTAMP") timestamp: String,
        @Header("ACCESS-PASSPHRASE") passphrase: String,
        @Query("coin") coin: String = "USDT"
    ): Response<BalanceResponse>

    @GET("api/v2/mix/position/all-position")
    suspend fun getAllPositions(
        @Header("ACCESS-KEY") ak: String,
        @Header("ACCESS-SIGN") sign: String,
        @Header("ACCESS-TIMESTAMP") timestamp: String,
        @Header("ACCESS-PASSPHRASE") passphrase: String,
        @Query("productType") productType: String = "USDT-FUTURES",
        @Query("marginCoin") marginCoin: String = "USDT"
    ): Response<PositionResponse>
}

data class BalanceResponse(
    val code: String,
    val msg: String,
    val data: List<BalanceData>
)

data class BalanceData(
    val available: String?,
    val hold: String?,
    @SerializedName("coinId") val coinId: String?,
    val coin: String?,
    val total: String?,
    val timestamp: Long?
)

data class PositionResponse(
    val code: String,
    val msg: String,
    val data: List<PositionData> = emptyList()
)

data class PositionData(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("marginCoin") val marginCoin: String,
    @SerializedName("holdSide") val holdSide: String,
    @SerializedName("openDelegateCount") val openDelegateCount: String,
    @SerializedName("marginSize") val marginSize: String,
    @SerializedName("available") val available: String,
    @SerializedName("locked") val locked: String,
    @SerializedName("total") val total: String,
    @SerializedName("leverage") val leverage: String,
    @SerializedName("achievedProfits") val achievedProfits: String,
    @SerializedName("averageOpenPrice") val averageOpenPrice: String,
    @SerializedName("marginMode") val marginMode: String,
    @SerializedName("holdMode") val holdMode: String,
    @SerializedName("unrealizedPL") val unrealizedPL: String,
    @SerializedName("liquidationPrice") val liquidationPrice: String,
    @SerializedName("keepMarginRate") val keepMarginRate: String,
    @SerializedName("marketPrice") val marketPrice: String,
    @SerializedName("cTime") val cTime: String,
    @SerializedName("uTime") val uTime: String
)
