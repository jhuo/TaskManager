package com.jhuo.taskmanager.auth.domain.repository

import com.jhuo.taskmanager.auth.data.remote.model.AuthResult
import com.jhuo.taskmanager.auth.data.remote.model.RefreshTokenResponse
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<Unit>
    suspend fun refreshToken(): Flow<Resource<RefreshTokenResponse>>
}