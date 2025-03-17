package com.jhuo.taskmanger.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhuo.taskmanger.data.remote.model.AuthResponse
import com.jhuo.taskmanger.data.remote.util.Resource
import com.jhuo.taskmanger.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<Resource<AuthResponse>>(Resource.Idle())
    val loginState: StateFlow<Resource<AuthResponse>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = repository.login(email, password)
        }
    }
}