package com.jhuo.taskmanager.auth.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        private const val KEY_ID_TOKEN = "id_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val EXPIRES_AT = "expires_at"
    }

    fun saveAuthTokens(idToken: String?, refreshToken: String?) {
        prefs.edit().apply {
            putString(KEY_ID_TOKEN, idToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun getIdToken(): String? = prefs.getString(KEY_ID_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun isAccessTokenExpired(): Boolean =
        System.currentTimeMillis() >= prefs.getLong(EXPIRES_AT, 0)
    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}
