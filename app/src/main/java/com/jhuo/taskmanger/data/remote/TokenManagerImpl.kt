package com.jhuo.taskmanger.data.remote

import android.content.SharedPreferences
import com.jhuo.taskmanger.data.remote.model.AuthResponse
import com.jhuo.taskmanger.data.remote.model.RefreshTokenResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(
    val sharedPreferences: SharedPreferences
) : TokenManager {

    private var accessTokenExpiry: Long = 0

    override fun saveTokens(authResponse: AuthResponse) {
        val expiresInMillis = authResponse.expiresIn.toLong() * 1000
        accessTokenExpiry = System.currentTimeMillis() + expiresInMillis

        sharedPreferences.edit()
            .putString("access_token", authResponse.idToken)
            .putString("refresh_token", authResponse.refreshToken)
            .putLong("token_expiry", accessTokenExpiry)
    }

    override fun saveRefreshedTokens(response: RefreshTokenResponse) {
        val expiresInMillis = response.expiresIn.toLong() * 1000
        accessTokenExpiry = System.currentTimeMillis() + expiresInMillis

        sharedPreferences.edit()
            .putString("access_token", response.accessToken)
            .putString("refresh_token", response.refreshToken)
            .putLong("token_expiry", accessTokenExpiry)
            .apply()
    }

    override fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)
    override fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)
    override fun isAccessTokenExpired(): Boolean = System.currentTimeMillis() > accessTokenExpiry

    override fun clearTokens() {
        sharedPreferences.edit().clear().apply()
        accessTokenExpiry = 0
    }
}