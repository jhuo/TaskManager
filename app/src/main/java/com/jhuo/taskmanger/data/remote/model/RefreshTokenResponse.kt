package com.jhuo.taskmanger.data.remote.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: String,
    @SerializedName("user_id") val userId: String
)
