package com.compose.sample.jwtauthapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.compose.sample.jwtauthapp.auth.AuthResult
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    val result by viewModel.result.collectAsState(initial = AuthResult.NoEvent)

    LaunchedEffect(Unit) {
        viewModel.authenticate()
    }
    LaunchedEffect(result) {
        when (result) {
            is AuthResult.Authorized -> {
                navController.navigate("secre_screen") {
                    popUpTo("auth_screen") {
                        inclusive = true
                    }
                }
            }

            AuthResult.UnAuthorized -> {
                Toast.makeText(context, "UnAuthorized", Toast.LENGTH_SHORT).show()
            }

            AuthResult.UnKnowError -> {
                Toast.makeText(context, "Unknow error occured", Toast.LENGTH_SHORT).show()
            }

            AuthResult.NoEvent -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = state.signUpUsername,
            onValueChange = {
                viewModel.onEvent(AuthUiEvent.SignUpUsernameChanged(it))
            },
            placeholder = {
                Text(text = "Username")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = state.signUpPassword,
            onValueChange = {
                viewModel.onEvent(AuthUiEvent.SignUpPasswordChanged(it))
            },
            placeholder = {
                Text(text = "Password")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            viewModel.onEvent(AuthUiEvent.SignUp)
        }) {
            Text(text = "Sign up")
        }

        Spacer(modifier = Modifier.height(64.dp))

        TextField(
            value = state.signInUsername,
            onValueChange = {
                viewModel.onEvent(AuthUiEvent.SignInUsernameChanged(it))
            },
            placeholder = {
                Text(text = "Username")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = state.signInPassword,
            onValueChange = {
                viewModel.onEvent(AuthUiEvent.SignInPasswordChanged(it))
            },
            placeholder = {
                Text(text = "Password")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            viewModel.onEvent(AuthUiEvent.SignIn)
        }) {
            Text(text = "Sign in")
        }
    }
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

}