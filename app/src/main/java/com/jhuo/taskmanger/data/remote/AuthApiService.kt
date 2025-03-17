package com.jhuo.taskmanger.data.remote

import com.jhuo.taskmanger.data.remote.model.AuthResponse
import com.jhuo.taskmanger.data.remote.model.LoginRequest
import com.jhuo.taskmanger.data.remote.model.RefreshTokenRequest
import com.jhuo.taskmanger.data.remote.model.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
        @FormUrlEncoded
        @POST("/v1/accounts:signInWithPassword")
        suspend fun login(
            @Query("key") apiKey: String = "AIzaSyA7sdREpS_iMUe-SPuJ9bJaa6sK1uEmjL4",
            @Field("email") email: String = "jerry08huo@yahoo.com",
            @Field("password") password: String = "Test@1234",
            @Field("returnSecureToken") returnSecureToken: Boolean = true,
        ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("token")
    suspend fun refreshToken(
        @Query("key") apiKey: String = "AIzaSyA7sdREpS_iMUe-SPuJ9bJaa6sK1uEmjL4",
        @Field("refresh_token") email: String = "AMf-vBy4_hFNVRiPcDdry-uk8OJe1ZBJuqayEJedj_Jm8jKXm05u7DOEkmDcZhtbLsGV5DCdI9x0KK0TiobdO1C4SyNF6Y9MytzlKJIN2apnxHUnIptR-107619D11s4Bs1PGLPS8bSAVAT-PeGo_g5jKH-tBR-3Tq6CUGpwzIqNYBtsrrk_UUVqwmPe9kxntEA0QzLQ_QnLx1oamEY5ySHokdYOzfo06Y1zOwOW3TCJsBWiHsmtWU8",
        @Field("grant_type") password: String = "refresh_token",
    ): Response<RefreshTokenResponse>
}