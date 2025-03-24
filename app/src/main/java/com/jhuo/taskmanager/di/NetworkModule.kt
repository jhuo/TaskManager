package com.jhuo.taskmanager.di

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.jhuo.taskmanager.auth.data.remote.AuthApiService
import com.jhuo.taskmanager.auth.data.remote.AuthInterceptor
import com.jhuo.taskmanager.auth.data.remote.RefreshTokenApiService
import com.jhuo.taskmanager.auth.data.remote.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val LOGIN_BASE_URL = "https://identitytoolkit.googleapis.com/"
    private const val REFRESH_BASE_URL = "https://securetoken.googleapis.com/"
    private const val TASK_MANAGER_BASE_URL = "https://task-manager-api-941148085453.us-central1.run.app/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Retrofit instance for Auth API endpoints (login)
    @Singleton
    @Provides
    @Named("LoginRetrofit")
    fun provideLoginRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(LOGIN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Retrofit instance for Refresh API endpoints
    @Singleton
    @Provides
    @Named("RefreshRetrofit")
    fun provideRefreshRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(REFRESH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Provide AuthApiService
    @Singleton
    @Provides
    fun provideAuthApiService(@Named("LoginRetrofit") retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    // Provide RefreshTokenApiService
    @Singleton
    @Provides
    fun provideRefreshTokenApiService(@Named("RefreshRetrofit") retrofit: Retrofit): RefreshTokenApiService =
        retrofit.create(RefreshTokenApiService::class.java)

    // Provide OkHttpClient for Task Manager API endpoints with interceptor and authenticator
    @Singleton
    @Provides
    @Named("TaskManagerOkHttpClient")
    fun provideTaskManagerOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }


    // Retrofit instance for Task Manager API endpoints
    @Singleton
    @Provides
    @Named("TaskManagerRetrofit")
    fun provideTaskManagerRetrofit(@Named("TaskManagerOkHttpClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(TASK_MANAGER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(app)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            app,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
