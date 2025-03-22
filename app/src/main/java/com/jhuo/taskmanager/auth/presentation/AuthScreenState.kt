package com.jhuo.taskmanager.auth.presentation

data class AuthScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)
