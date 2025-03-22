package com.jhuo.taskmanager.auth.data.remote.model

sealed class AuthResult<T>(val data: T? = null) {
    class Authorized<T>(data: T? = null): AuthResult<T>(data)
    class Unauthorized<T>: AuthResult<T>()
    class InvalidInput<T>: AuthResult<T>()
    class UnknownError<T>: AuthResult<T>()
}