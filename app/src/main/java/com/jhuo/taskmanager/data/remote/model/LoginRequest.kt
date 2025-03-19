package com.jhuo.taskmanager.data.remote.model


data class LoginRequest(
    val email: String = "jerry08huo@yahoo.com",
    val password: String = "Test@1234",
    val returnSecureToken: Boolean = true
)
