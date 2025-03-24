package com.jhuo.taskmanager.auth.data.remote

import com.jhuo.taskmanager.auth.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val refreshTokenApiService: RefreshTokenApiService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: return null
        val newTokens = runBlocking {
            val refreshResponse = refreshTokenApiService.refreshToken(refreshToken)
            if (refreshResponse.isSuccessful) refreshResponse.body() else null
        } ?: return null

        val newAccessToken = newTokens.idToken
        val newRefreshToken = newTokens.refreshToken
        val expiresIn = newTokens.expiresIn?.toLong()

        return if (newAccessToken != null && newRefreshToken != null && expiresIn != null) {
            tokenManager.saveAuthTokens(newAccessToken, newRefreshToken, expiresIn)
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            tokenManager.clearTokens()
            null
        }
    }
}
