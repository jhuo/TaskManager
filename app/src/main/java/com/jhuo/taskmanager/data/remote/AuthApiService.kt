package com.jhuo.taskmanager.data.remote

import com.jhuo.taskmanager.data.remote.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
        @FormUrlEncoded
        @POST("v1/accounts:signInWithPassword")
        suspend fun login(
            @Field("email") email: String = "jerry08huo@yahoo.com",
            @Field("password") password: String = "Test@1234",
            @Field("returnSecureToken") returnSecureToken: Boolean = true,
            @Query("key") apiKey: String = "AIzaSyA7sdREpS_iMUe-SPuJ9bJaa6sK1uEmjL4",
        ): Response<AuthResponse>
}