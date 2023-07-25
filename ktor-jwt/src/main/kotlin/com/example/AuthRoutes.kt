package com.example

import com.example.authenticate
import com.example.data.requests.AuthRequest
import com.example.data.responses.AuthResponse
import com.example.data.user.User
import com.example.data.user.UserDataSource
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("signup") {
        val request = call.receive(AuthRequest::class)

        val areFieldBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 6
        if (areFieldBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val isAcknowledged = userDataSource.insertUser(user)
        if (!isAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    config: TokenConfig
) {
    post("signin") {
        val request = call.receive(AuthRequest::class)

        val user = userDataSource.getUserByUsername(request.username) ?: run {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            config = config,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(HttpStatusCode.OK, message = AuthResponse(token))
    }
}


fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}
