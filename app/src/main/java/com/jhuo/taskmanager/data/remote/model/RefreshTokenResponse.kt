package com.jhuo.taskmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("expires_in") val expiresIn: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("id_token") val idToken: String?,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("project_id") val projectId: String?
)
