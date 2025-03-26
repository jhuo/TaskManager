package com.jhuo.taskmanager.auth.presentation

sealed class AuthUiEvent {
    sealed class Input {
        data class EnterEmail(val value: String): AuthUiEvent()
        data class EnterPassword(val value: String): AuthUiEvent()
    }
    sealed class ButtonClick {
        data object SignIn: AuthUiEvent()
    }
    sealed class Navigate {
        data object TaskList: AuthUiEvent()
    }
    data class ShowSnackBar(val message: String): AuthUiEvent()
    data object ClearEmailError: AuthUiEvent()
    data object ClearPasswordError: AuthUiEvent()

}