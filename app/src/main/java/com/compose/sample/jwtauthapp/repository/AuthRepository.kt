package com.compose.sample.jwtauthapp.repository

import com.compose.sample.jwtauthapp.auth.AuthResult

interface AuthRepository {
    suspend fun signUp(username: String, password: String): AuthResult<Unit>
    suspend fun signIn(username: String, password: String): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>

}