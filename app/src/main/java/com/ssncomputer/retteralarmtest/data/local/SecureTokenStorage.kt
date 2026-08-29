package com.ssncomputer.retteralarmtest.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Immutable snapshot of the currently stored auth tokens. */
data class AuthTokens(val accessToken: String, val refreshToken: String)

/**
 * Persists access/refresh tokens in EncryptedSharedPreferences (AES256-GCM, Android Keystore
 * backed master key). Never store tokens in plain SharedPreferences or logs.
 */
@Singleton
class SecureTokenStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "smartwatch_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @Synchronized
    fun getTokens(): AuthTokens? {
        val access = prefs.getString(KEY_ACCESS_TOKEN, null)
        val refresh = prefs.getString(KEY_REFRESH_TOKEN, null)
        return if (access != null && refresh != null) AuthTokens(access, refresh) else null
    }

    private val _isLoggedIn = MutableStateFlow(getTokens() != null)

    /**
     * Emits whether all required auth headers are available. [TokenAuthenticator] clears the
     * tokens when a refresh fails, so expiry automatically flips this back to `false`.
     */
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    @Synchronized
    fun saveTokens(tokens: AuthTokens) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            .putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
            .apply()
        _isLoggedIn.value = true
    }

    @Synchronized
    fun clear() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
