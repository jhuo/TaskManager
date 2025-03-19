package com.jhuo.taskmanager.data.repository

import com.jhuo.taskmanager.data.remote.model.AuthResponse
import com.jhuo.taskmanager.data.remote.model.RefreshTokenResponse
import com.jhuo.taskmanager.data.remote.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<AuthResponse>>
    fun refreshToken(): Flow<Resource<RefreshTokenResponse>>
}