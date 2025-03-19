package com.jhuo.taskmanager.data.repository

import com.jhuo.taskmanager.data.local.TokenManager
import com.jhuo.taskmanager.data.remote.AuthApiService
import com.jhuo.taskmanager.data.remote.RefreshTokenApiService
import com.jhuo.taskmanager.data.remote.model.AuthResponse
import com.jhuo.taskmanager.data.remote.model.RefreshTokenResponse
import com.jhuo.taskmanager.data.remote.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val refreshTokenApiService: RefreshTokenApiService,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<AuthResponse>> = flow {
        try {
            val response = authApiService.login( email, password)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    authResponse.idToken?.let { idToken ->
                        authResponse.refreshToken?.let { refreshToken ->
                            authResponse.expiresIn?.let { expiresIn ->
                                tokenManager.saveAuthTokens(idToken, refreshToken, expiresIn)
                            }
                        }
                    }
                    emit(Resource.Success(authResponse))
                } ?: emit(Resource.Error(("Empty response")))
            } else {
                emit(Resource.Error("Http Error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun refreshToken(): Flow<Resource<RefreshTokenResponse>> = flow {
        val refreshTokenValue = tokenManager.getRefreshToken()
        if (refreshTokenValue == null) {
            emit(Resource.Error("No refresh token available"))
            return@flow
        }
        try {
            val response = refreshTokenApiService.refreshToken( refreshTokenValue)
            if (response.isSuccessful) {
                response.body()?.let { refreshResponse ->
                    refreshResponse.idToken?.let { idToken ->
                        refreshResponse.refreshToken?.let { newRefreshToken ->
                            refreshResponse.expiresIn?.let { expiresIn->
                                tokenManager.saveAuthTokens(idToken, newRefreshToken, expiresIn)
                            }

                        }
                    }
                    emit(Resource.Success(refreshResponse))
                } ?: emit(Resource.Error("Empty response"))
            } else {
                emit(Resource.Error("Http Error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}
