package com.jhuo.taskmanager.auth.data.repository

import com.jhuo.taskmanager.auth.data.remote.AuthApiService
import com.jhuo.taskmanager.auth.data.remote.RefreshTokenApiService
import com.jhuo.taskmanager.auth.data.remote.model.AuthResult
import com.jhuo.taskmanager.auth.data.remote.model.RefreshTokenResponse
import com.jhuo.taskmanager.auth.domain.repository.AuthRepository
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val refreshTokenApiService: RefreshTokenApiService,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult<Unit> {
        return try {
            val response = authApiService.login(email, password) //.login("jerry08huo+1@yahoo.com", "Test@1234") //.login(email, password)
            tokenManager.saveAuthTokens(response.idToken, response.refreshToken, response.expiresIn?.toLong())
            AuthResult.Authorized()
        } catch(e: HttpException) {
            when(e.code()) {
                400 -> AuthResult.InvalidInput()
                401, 403 -> AuthResult.Unauthorized()
                else ->  AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.UnknownError()
        }
    }

    override suspend fun refreshToken(): Flow<Resource<RefreshTokenResponse>> = flow {
        val refreshTokenValue = tokenManager.getRefreshToken()
        if (refreshTokenValue == null) {
            emit(Resource.Error("No refresh token available"))
            return@flow
        }
        try {
            val response = refreshTokenApiService.refreshToken( refreshTokenValue)
            if (response.isSuccessful) {
                response.body()?.let { refreshResponse ->
                    val newAccessToken = refreshResponse.idToken
                    val newRefreshToken = refreshResponse.refreshToken
                    val expiresIn = refreshResponse.expiresIn?.toLong()
                    if (newAccessToken != null && newRefreshToken != null && expiresIn != null) {
                        tokenManager.saveAuthTokens(newAccessToken, newRefreshToken, expiresIn)
                        emit(Resource.Success(refreshResponse))
                    } else {
                        emit(Resource.Error("Invalid refresh response"))
                    }
                } ?: emit(Resource.Error("Empty response"))
            } else {
                when (response.code()) {
                    401, 403 -> {
                        tokenManager.clearTokens()
                        emit(Resource.Error("Unauthorized: Refresh token expired"))
                    }
                    else -> emit(Resource.Error("HTTP Error: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}
