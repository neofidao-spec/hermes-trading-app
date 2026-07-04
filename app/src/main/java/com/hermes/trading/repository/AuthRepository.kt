package com.hermes.trading.repository

import com.hermes.trading.api.BitgetApi
import com.hermes.trading.api.PositionData
import com.hermes.trading.datastore.SecurePreferences
import kotlinx.coroutines.flow.first
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val api: BitgetApi
) {
    suspend fun saveCredentials(akValue: String, secValue: String, passValue: String) {
        securePreferences.saveCredentials(akValue, secValue, passValue)
    }

    suspend fun clearCredentials() {
        securePreferences.clearCredentials()
    }

    suspend fun fetchBalance(): Double {
        val creds = securePreferences.credentials.first()
            ?: throw IllegalStateException("No credentials saved. Please login first.")

        val timestamp = System.currentTimeMillis().toString()
        val sign = signRequest(creds.apiSecret, timestamp, "GET", "/api/v5/account/balance", "")

        val response = api.getBalance(
            ak = creds.ak,
            sign = sign,
            timestamp = timestamp,
            passphrase = creds.passphrase
        )

        if (!response.isSuccessful) {
            throw RuntimeException("Balance HTTP ${response.code()}: ${response.errorBody()?.string()}")
        }
        val body = response.body() ?: throw RuntimeException("Empty balance response")
        if (body.code != "00000") {
            throw RuntimeException("Balance API error: ${body.msg} (${body.code})")
        }
        val usdt = body.data.firstOrNull { it.coin == "USDT" }
        return (usdt?.total?.toDoubleOrNull() ?: usdt?.available?.toDoubleOrNull() ?: 0.0)
    }

    suspend fun fetchPositions(): List<PositionData> {
        val creds = securePreferences.credentials.first()
            ?: throw IllegalStateException("No credentials saved. Please login first.")

        val timestamp = System.currentTimeMillis().toString()
        val sign = signRequest(creds.apiSecret, timestamp, "GET", "/api/v2/mix/position/all-position", "")

        val response = api.getAllPositions(
            ak = creds.ak,
            sign = sign,
            timestamp = timestamp,
            passphrase = creds.passphrase
        )

        if (!response.isSuccessful) {
            throw RuntimeException("Positions HTTP ${response.code()}: ${response.errorBody()?.string()}")
        }
        val body = response.body() ?: throw RuntimeException("Empty positions response")
        if (body.code != "00000") {
            throw RuntimeException("Positions API error: ${body.msg} (${body.code})")
        }
        return body.data.filter { (it.total.toDoubleOrNull() ?: 0.0) > 0.0 }
    }

    /**
     * Bitget signing: HMAC-SHA256(secret, timestamp + method + requestPath + body)
     * Returns base64-encoded signature.
     */
    private fun signRequest(
        secret: String,
        timestamp: String,
        method: String,
        requestPath: String,
        body: String
    ): String {
        val prehash = timestamp + method.uppercase() + requestPath + body
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val rawHmac = mac.doFinal(prehash.toByteArray(Charsets.UTF_8))
        return android.util.Base64.encodeToString(rawHmac, android.util.Base64.NO_WRAP)
    }
}
