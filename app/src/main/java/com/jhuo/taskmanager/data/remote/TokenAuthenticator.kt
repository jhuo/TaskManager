package com.jhuo.taskmanager.data.remote

import android.R.attr.apiKey
import com.jhuo.taskmanager.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val refreshTokenApiService: RefreshTokenApiService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loops by checking the number of prior attempts.
        if (responseCount(response) >= 3) return null

        val currentRefreshToken = tokenManager.getRefreshToken() ?: return null

        return try {
            val refreshResponse = runBlocking {
                refreshTokenApiService.refreshToken(
                    refreshToken = currentRefreshToken,
                )
            }
            if (refreshResponse.isSuccessful) {
                val newTokens = refreshResponse.body()
                if (newTokens?.idToken != null && newTokens.refreshToken != null && newTokens.expiresIn != null) {
                    // Update stored tokens
                    tokenManager.saveAuthTokens(newTokens.idToken, newTokens.refreshToken, newTokens.expiresIn)
                    // Retry the original request with the new token
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.idToken}")
                        .build()
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
