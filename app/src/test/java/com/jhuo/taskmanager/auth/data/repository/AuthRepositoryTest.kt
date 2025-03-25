package com.jhuo.taskmanager.auth.data.repository

import com.jhuo.taskmanager.auth.data.remote.AuthApiService
import com.jhuo.taskmanager.auth.data.remote.RefreshTokenApiService
import com.jhuo.taskmanager.auth.data.remote.model.AuthResponse
import com.jhuo.taskmanager.auth.data.remote.model.AuthResult
import com.jhuo.taskmanager.auth.data.remote.model.AuthResult.Authorized
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AuthRepositoryTest {

    private lateinit var repository: AuthRepositoryImpl
    private val authApiService: AuthApiService = mockk()
    private val refreshTokenApiService: RefreshTokenApiService = mockk()
    private val tokenManager: TokenManager = mockk()

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(authApiService, refreshTokenApiService, tokenManager)
    }

    @Test
    fun `login should return AuthResult Authorized when login is successful`() = runTest {
        
        val email = "test@example.com"
        val password = "password123"
        val authResponse = AuthResponse(
            idToken = "token123",
            refreshToken = "refresh123",
            expiresIn = "3600",
            localId = "1234",
            email = "test@example.com",
            registered = true
        )
        coEvery { authApiService.login(email, password) } returns authResponse
        coEvery { tokenManager.saveAuthTokens("token123", "refresh123", 3600L) } returns Unit

        
        val result = repository.login(email, password)

        
        assertTrue(result is Authorized<Unit> )
        coVerify { tokenManager.saveAuthTokens("token123", "refresh123", 3600L) }
    }

    @Test
    fun `login should return AuthResult UnknownError when an exception occurs`() = runTest {
        
        val email = "test@example.com"
        val password = "password123"
        coEvery { authApiService.login(email, password) } throws Exception("Unknown error")

        
        val result = repository.login(email, password)

        
        assertTrue(result is AuthResult.UnknownError<Unit>)
    }

    @Test
    fun `refreshToken should emit Error when refresh token is null`() = runTest {
        
        coEvery { tokenManager.getRefreshToken() } returns null

        
        val result = repository.refreshToken().first()

        
        assertEquals("No refresh token available", result.message)
    }

    @Test
    fun `refreshToken should emit Error when refresh token API call fails`() = runTest {
        
        val refreshToken = "refresh123"
        coEvery { tokenManager.getRefreshToken() } returns refreshToken
        coEvery { refreshTokenApiService.refreshToken(refreshToken) } throws Exception("API error")

        
        val result = repository.refreshToken().first()

        
        assertEquals("API error", result.message)
    }
}