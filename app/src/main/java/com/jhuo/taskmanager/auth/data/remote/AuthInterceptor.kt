package com.jhuo.taskmanager.auth.data.remote

import com.jhuo.taskmanager.auth.data.repository.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.header("No-Authentication") != null) {
            return chain.proceed(request)
        }

        val token = tokenManager.getIdToken() ?: return chain.proceed(request)

        return chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )
    }
}