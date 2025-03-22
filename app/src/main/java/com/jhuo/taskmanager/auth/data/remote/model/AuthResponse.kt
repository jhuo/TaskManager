package com.jhuo.taskmanager.auth.data.remote.model

data class AuthResponse(
    val idToken: String?,
    val refreshToken: String?,
    val expiresIn: String?,
    val localId: String?,
    val email: String?,
    val registered: Boolean?
)
