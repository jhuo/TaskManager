package com.jhuo.taskmanger.data.remote.model


data class RefreshTokenRequest(
    val refresh_token: String,
    val grant_type: String = "refresh_token"
)