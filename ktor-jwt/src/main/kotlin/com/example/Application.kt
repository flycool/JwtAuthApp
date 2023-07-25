package com.example

import com.example.data.user.MongoUserDataSource
import io.ktor.server.application.*
import com.example.plugins.*
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    val db = KMongo.createClient().coroutine.getDatabase("jwt_db")
    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 30 * 60 * 60 * 24 * 1000L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashService = SHA256HashingService()

    configureSecurity(tokenConfig)
    configureMonitoring()
    configureSerialization()
    configureRouting(userDataSource, hashService, tokenService, tokenConfig)
}
