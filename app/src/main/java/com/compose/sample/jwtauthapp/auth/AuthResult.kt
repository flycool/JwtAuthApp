package com.compose.sample.jwtauthapp.auth

sealed class AuthResult<T>(val data: T? = null) {
    class Authorized<T>(data: T? = null) : AuthResult<T>(data)
    object UnAuthorized : AuthResult<Unit>()
    object UnKnowError : AuthResult<Unit>()
    object NoEvent : AuthResult<Unit>()
}

