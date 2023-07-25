package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtTokenService : TokenService {
    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String {
        return JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn)).also { token ->
                claims.forEach { claim ->
                    token.withClaim(claim.name, claim.value)
                }
            }.run {
                sign(Algorithm.HMAC256(config.secret))
            }
    }
}