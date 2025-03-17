package com.jhuo.taskmanger.data.repository

import com.jhuo.taskmanger.data.remote.AuthApiService
import com.jhuo.taskmanger.data.remote.TokenManager
import com.jhuo.taskmanger.data.remote.model.AuthResponse
import com.jhuo.taskmanger.data.remote.util.Resource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    object RetrofitClient {
        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://identitytoolkit.googleapis.com") // Ensure trailing slash
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val api: AuthApiService = retrofit.create(AuthApiService::class.java)
    }
    override suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = RetrofitClient.api.login()
            if (response.isSuccessful) {
                response.body()?.let {
//                    tokenManager.saveTokens(it)
                    Resource.Success(it)
                } ?: Resource.Error("Empty response body")
            } else {
                Resource.Error("Login failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}