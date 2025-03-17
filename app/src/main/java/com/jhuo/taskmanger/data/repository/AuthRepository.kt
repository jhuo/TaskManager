package com.jhuo.taskmanger.data.repository

import com.jhuo.taskmanger.data.remote.model.AuthResponse
import com.jhuo.taskmanger.data.remote.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<AuthResponse>
}