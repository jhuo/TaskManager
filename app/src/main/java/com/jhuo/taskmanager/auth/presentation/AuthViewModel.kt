package com.jhuo.taskmanager.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhuo.taskmanager.auth.data.remote.model.AuthResult
import com.jhuo.taskmanager.auth.data.repository.TokenManager
import com.jhuo.taskmanager.auth.domain.repository.AuthRepository
import com.jhuo.taskmanager.auth.presentation.util.AuthStrings.AUTH_INVALID_ERROR
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
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
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

            else -> {}
        }
    }
    private fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.login(_state.value.email, _state.value.password)
            _state.update { it.copy(isLoading = false) }
            when(result) {
                is AuthResult.Authorized -> {
                    _event.emit(AuthUiEvent.Navigate.Home)
                    _state.update { it.copy(password = "") }
                }
                is AuthResult.Unauthorized -> _event.emit(AuthUiEvent.ShowSnackBar(AUTH_UNAUTHORIZED_ERROR))
                is AuthResult.UnknownError -> _event.emit(AuthUiEvent.ShowSnackBar(AUTH_UNKNOWN_ERROR))
                is AuthResult.InvalidInput -> _event.emit(AuthUiEvent.ShowSnackBar(AUTH_INVALID_ERROR))
            }
        }
    }

    fun logout() {
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getIdToken() != null && !tokenManager.isAccessTokenExpired()
    }

}
