package com.github.arhor.dgs.users.api.routes

import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.security.KeyPair
import java.security.PublicKey
import java.util.*

@Component
class MainRouter(jwtSigningKeyPair: KeyPair) : RouterFunction<ServerResponse> by router({

    GET(pattern = "favicon.ico") {
        ServerResponse
            .noContent()
            .build()
    }
    GET(pattern = "public-key") {
        ServerResponse
            .ok()
            .body(jwtSigningKeyPair.public.asPemString())
    }
})

private fun PublicKey.asPemString(): String = """
    -----BEGIN PUBLIC KEY-----
    ${Base64.getEncoder().encodeToString(encoded)}
    -----END PUBLIC KEY-----
    """.trimIndent()
