package com.jhuo.taskmanager.auth.presentation

import com.jhuo.taskmanager.auth.data.remote.model.AuthResult
import com.jhuo.taskmanager.auth.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val authRepository: AuthRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn should emit Navigate Home event when login is successful`() = runTest {
        
        val email = "test@example.com"
        val password = "password123"
        coEvery { authRepository.login(email, password) } returns AuthResult.Authorized()

        
        viewModel.onEvent(AuthUiEvent.Input.EnterEmail(email))
        viewModel.onEvent(AuthUiEvent.Input.EnterPassword(password))
        viewModel.onEvent(AuthUiEvent.ButtonClick.SignIn)

        
        val event = viewModel.event.first()
        assertEquals(AuthUiEvent.Navigate.Home, event)
    }

    @Test
    fun `signIn should emit ShowSnackBar event with unauthorized error when login fails with 401`() = runTest {
        
        val email = "test@example.com"
        val password = "password123"
        coEvery { authRepository.login(email, password) } returns AuthResult.Unauthorized()

        
        viewModel.onEvent(AuthUiEvent.Input.EnterEmail(email))
        viewModel.onEvent(AuthUiEvent.Input.EnterPassword(password))
        viewModel.onEvent(AuthUiEvent.ButtonClick.SignIn)

        
        val event = viewModel.event.first()
        assertEquals(AuthUiEvent.ShowSnackBar("Unauthorized"), event)
    }

    @Test
    fun `signIn should emit ShowSnackBar event with invalid input error when login fails with 400`() = runTest {
        
        val email = "test@example.com"
        val password = "password123"
        coEvery { authRepository.login(email, password) } returns AuthResult.InvalidInput()

        
        viewModel.onEvent(AuthUiEvent.Input.EnterEmail(email))
        viewModel.onEvent(AuthUiEvent.Input.EnterPassword(password))
        viewModel.onEvent(AuthUiEvent.ButtonClick.SignIn)

        
        val event = viewModel.event.first()
        assertEquals(AuthUiEvent.ShowSnackBar("Invalid Email and Password"), event)
    }

    @Test
    fun `signIn should emit ShowSnackBar event with unknown error when login fails with an exception`() = runTest {
        
        val email = "test@example.com"
        val password = "password123"
        coEvery { authRepository.login(email, password) } returns AuthResult.UnknownError()

        
        viewModel.onEvent(AuthUiEvent.Input.EnterEmail(email))
        viewModel.onEvent(AuthUiEvent.Input.EnterPassword(password))
        viewModel.onEvent(AuthUiEvent.ButtonClick.SignIn)

        
        val event = viewModel.event.first()
        assertEquals(AuthUiEvent.ShowSnackBar("Unknown error"), event)
    }
}