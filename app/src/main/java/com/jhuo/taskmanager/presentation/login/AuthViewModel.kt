package com.jhuo.taskmanager.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhuo.taskmanager.data.remote.model.AuthResponse
import com.jhuo.taskmanager.data.remote.util.Resource
import com.jhuo.taskmanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<AuthResponse>?>(Resource.Idle())
    val authState: StateFlow<Resource<AuthResponse>?> = _authState

    fun login(email: String, password: String) {
        authRepository.login(email, password).onEach { result ->
            _authState.value = result
        }.launchIn(viewModelScope)
    }

    // The refreshToken() method is available for background usage if needed.
    fun refreshToken() {
        authRepository.refreshToken().onEach { /* handle refresh result silently */ }
            .launchIn(viewModelScope)
    }
}
