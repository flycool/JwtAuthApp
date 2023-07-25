package com.compose.sample.jwtauthapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.sample.jwtauthapp.auth.AuthResult
import com.compose.sample.jwtauthapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(AuthState())

    private val _result = MutableSharedFlow<AuthResult<Unit>>()
    val result = _result.asSharedFlow()

    init {
        authenticate()
    }

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            AuthUiEvent.SignIn -> {
                signIn()
            }

            is AuthUiEvent.SignInPasswordChanged -> {
                state = state.copy(signInPassword = event.value)
            }

            is AuthUiEvent.SignInUsernameChanged -> {
                state = state.copy(signInUsername = event.value)
            }

            AuthUiEvent.SignUp -> {
                signUp()
            }

            is AuthUiEvent.SignUpPasswordChanged -> {
                state = state.copy(signUpPassword = event.value)
            }

            is AuthUiEvent.SignUpUsernameChanged -> {
                state = state.copy(signUpUsername = event.value)
            }
        }
    }

    private fun signUp() {
        authLaunch {
            repository.signUp(state.signUpUsername, state.signUpPassword)
        }
    }

    private fun signIn() {
        authLaunch {
            repository.signIn(state.signInUsername, state.signInPassword)
        }
    }

    private fun authenticate() {
        authLaunch {
            repository.authenticate()
        }
    }

    private fun authLaunch(block: suspend () -> AuthResult<Unit>) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            _result.emit(block())
            state = state.copy(isLoading = false)
        }
    }
}