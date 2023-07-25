package com.example.plugins

import com.example.authenticate
import com.example.data.user.UserDataSource
import com.example.getSecretInfo
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.signIn
import com.example.signUp
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    config: TokenConfig
) {
    routing {
        get("/") {
            call.respond("hgg")
        }
        signIn(userDataSource, hashingService, tokenService, config)
        signUp(hashingService, userDataSource)
        authenticate()
        getSecretInfo()
    }
}
