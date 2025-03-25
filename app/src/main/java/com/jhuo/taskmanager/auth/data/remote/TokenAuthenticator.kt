package com.jhuo.taskmanager.auth.data.remote

import com.jhuo.taskmanager.auth.data.repository.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val refreshTokenApiService: RefreshTokenApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            try {
                val refreshToken = tokenManager.getRefreshToken() ?: return@runBlocking null

                val refreshResponse = refreshTokenApiService.refreshToken(refreshToken)

                if (refreshResponse.isSuccessful) {
                    refreshResponse.body()?.let { tokens ->
                        tokenManager.saveAuthTokens(
                            tokens.idToken,
                            tokens.refreshToken ?: refreshToken,
                            tokens.expiresIn?.toLong()
                        )
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${tokens.idToken}")
                            .build()
                    }
                } else {
                    tokenManager.clearTokens()
                    null
                }
            } catch (e: Exception) {
                tokenManager.clearTokens()
                null
            }
        }
    }
}
