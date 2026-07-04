package com.hermes.trading.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFERENCES_NAME = "auth_prefs"

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

@Singleton
class SecurePreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.authDataStore

    private val KEY_API = stringPreferencesKey("api_key")
    private val KEY_SECRET = stringPreferencesKey("api_secret")
    private val KEY_PASSPHRASE = stringPreferencesKey("api_passphrase")

    suspend fun saveCredentials(apiKeyValue: String, secretValue: String, passphraseValue: String) {
        dataStore.edit { prefs ->
            prefs[KEY_API] = apiKeyValue
            prefs[KEY_SECRET] = secretValue
            prefs[KEY_PASSPHRASE] = passphraseValue
        }
    }

    suspend fun clearCredentials() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_API)
            prefs.remove(KEY_SECRET)
            prefs.remove(KEY_PASSPHRASE)
        }
    }

    data class AuthCreds(val ak: String, val apiSecret: String, val passphrase: String)

    val credentials: Flow<AuthCreds?> = dataStore.data.map { prefs ->
        val key = prefs[KEY_API]
        val secret = prefs[KEY_SECRET]
        val phrase = prefs[KEY_PASSPHRASE]
        if (key != null && secret != null && phrase != null) {
            AuthCreds(key, secret, phrase)
        } else {
            null
        }
    }
}
