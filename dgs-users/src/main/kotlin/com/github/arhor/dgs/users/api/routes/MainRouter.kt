package com.github.arhor.dgs.users.api.routes

import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.security.KeyPair
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
            .body(
                """
                -----BEGIN PUBLIC KEY-----
                ${Base64.getEncoder().encodeToString(jwtSigningKeyPair.public.encoded)}
                -----END PUBLIC KEY-----
                """.trimIndent()
            )
    }
})
