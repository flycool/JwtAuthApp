package com.compose.sample.jwtauthapp.repository

import android.content.SharedPreferences
import com.compose.sample.jwtauthapp.auth.AuthApi
import com.compose.sample.jwtauthapp.auth.AuthRequest
import com.compose.sample.jwtauthapp.auth.AuthResult
import retrofit2.HttpException
import java.lang.Exception

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefs: SharedPreferences
) : AuthRepository {

    override suspend fun signUp(username: String, password: String): AuthResult<Unit> {
        return tryWith {
            api.signUp(
                AuthRequest(username = username, password = password)
            )
            signIn(username, password)
        }
    }

    override suspend fun signIn(username: String, password: String): AuthResult<Unit> {
        return tryWith {
            val response = api.signIn(
                request = AuthRequest(username, password)
            )
            prefs.edit().putString("jwt", response.token).apply()
            AuthResult.Authorized()
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return tryWith {
            val token = prefs.getString("jwt", null) ?: return@tryWith AuthResult.UnAuthorized
            api.authenticate("Bearer $token")
            AuthResult.Authorized()
        }
    }

    private suspend fun tryWith(block: suspend () -> AuthResult<Unit>): AuthResult<Unit> = try {
        block()
    } catch (ex: HttpException) {
        if (ex.code() == 401) {
            AuthResult.UnAuthorized
        } else {
            AuthResult.UnKnowError
        }
    } catch (e: Exception) {
        AuthResult.UnKnowError
    }
}