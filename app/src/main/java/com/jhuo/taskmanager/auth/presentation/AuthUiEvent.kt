package com.jhuo.taskmanager.auth.presentation

sealed class AuthUiEvent {
    data object Idle: AuthUiEvent()
    sealed class Input {
        data class EnterEmail(val value: String): AuthUiEvent()
        data class EnterPassword(val value: String): AuthUiEvent()
    }
    sealed class ButtonClick {
        data object SignIn: AuthUiEvent()
    }

    sealed class Navigate {
        data object Home: AuthUiEvent()
    }

    data class ShowSnackBar(val message: String): AuthUiEvent()
}