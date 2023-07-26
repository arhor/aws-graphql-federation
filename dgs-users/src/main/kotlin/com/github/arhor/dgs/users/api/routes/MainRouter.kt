package com.github.arhor.dgs.users.api.routes

import com.github.arhor.dgs.users.service.TokenProvider
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@Component
class MainRouter(private val tokenProvider: TokenProvider) : RouterFunction<ServerResponse> by router({

    GET("favicon.ico") {
        ServerResponse
            .noContent()
            .build()
    }
    GET("public-key") {
        ServerResponse
            .ok()
            .body(tokenProvider.activePublicKey())
    }
})
