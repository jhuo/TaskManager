package com.jhuo.taskmanager.auth.data.remote

import com.jhuo.taskmanager.auth.data.remote.model.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface RefreshTokenApiService {
    @FormUrlEncoded
    @POST("v1/token")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String, //= "AMf-vBy4_hFNVRiPcDdry-uk8OJe1ZBJuqayEJedj_Jm8jKXm05u7DOEkmDcZhtbLsGV5DCdI9x0KK0TiobdO1C4SyNF6Y9MytzlKJIN2apnxHUnIptR-107619D11s4Bs1PGLPS8bSAVAT-PeGo_g5jKH-tBR-3Tq6CUGpwzIqNYBtsrrk_UUVqwmPe9kxntEA0QzLQ_QnLx1oamEY5ySHokdYOzfo06Y1zOwOW3TCJsBWiHsmtWU8",
        @Field("grant_type") grantType: String = "refresh_token",
        @Query("key") apiKey: String = "AIzaSyA7sdREpS_iMUe-SPuJ9bJaa6sK1uEmjL4",
    ): Response<RefreshTokenResponse>
}