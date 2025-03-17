package com.jhuo.taskmanger.data.remote

import com.jhuo.taskmanger.data.remote.model.AuthResponse
import com.jhuo.taskmanger.data.remote.model.RefreshTokenResponse

interface TokenManager {
    fun saveTokens(authResponse: AuthResponse)
    fun saveRefreshedTokens(response: RefreshTokenResponse)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
    fun isAccessTokenExpired(): Boolean
}