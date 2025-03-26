package com.jhuo.taskmanager.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhuo.taskmanager.auth.data.remote.model.AuthResult
import com.jhuo.taskmanager.auth.domain.repository.AuthRepository
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_INVALID_EMAIL_ERROR
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_INVALID_ERROR
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_INVALID_PASSWORD_ERROR
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_UNAUTHORIZED_ERROR
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_UNKNOWN_ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AuthScreenState())
    val state: StateFlow<AuthScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthScreenState()
    )
    private val _event = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    fun onEvent(event: AuthUiEvent) {
        when(event) {
            AuthUiEvent.ButtonClick.SignIn -> {
                signIn()
            }
            is AuthUiEvent.Input.EnterEmail -> {
                _state.update { it.copy(email = event.value) }
            }
            is AuthUiEvent.Input.EnterPassword -> {
                _state.update { it.copy(password = event.value) }
            }
            is AuthUiEvent.ClearEmailError -> {
                _state.update { it.copy(emailError = null) }
            }
            is AuthUiEvent.ClearPasswordError -> {
                _state.update { it.copy(passwordError = null) }
            }
            else -> {}
        }
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return if (email.isBlank() || !emailRegex.matches(email)) {
            AUTH_INVALID_EMAIL_ERROR
        } else {
            null
        }
    }

    private fun validatePassword(password: String): String? {
        return if (password.isBlank() || password.length < 8) {
            AUTH_INVALID_PASSWORD_ERROR
        } else {
            null
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            val emailError = validateEmail(_state.value.email)
            val passwordError = validatePassword(_state.value.password)
            _state.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            if (_state.value.emailError != null || _state.value.passwordError != null) {
                _event.emit(AuthUiEvent.ShowSnackBar(AUTH_INVALID_ERROR))
                return@launch
            }

            _state.update { it.copy(isLoading = true) }
            val result = authRepository.login(_state.value.email, _state.value.password)
            _state.update { it.copy(isLoading = false) }
            when(result) {
                is AuthResult.Authorized -> {
                    _event.emit(AuthUiEvent.Navigate.TaskList)
                    _state.update { it.copy(password = "") }
                }
                is AuthResult.Unauthorized -> _event.emit(AuthUiEvent.ShowSnackBar(AUTH_UNAUTHORIZED_ERROR))
                is AuthResult.UnknownError -> _event.emit(AuthUiEvent.ShowSnackBar(AUTH_UNKNOWN_ERROR))
                is AuthResult.InvalidInput -> _event.emit(AuthUiEvent.ShowSnackBar(AUTH_INVALID_ERROR))
            }
        }
    }
}
