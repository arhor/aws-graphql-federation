package com.github.arhor.dgs.users.api.routes

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.dgs.users.service.TokenProvider
import com.github.arhor.dgs.users.service.UserService
import com.github.arhor.dgs.users.service.dto.CurrentUserRequest
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router

@Component
class MainRouter(
    private val tokenProvider: TokenProvider,
    private val userService: UserService,
) : RouterFunction<ServerResponse> by router({

    GET("favicon.ico") {
        ServerResponse
            .noContent()
            .build()
    }
    GET("public-key") {
        val publicKey = tokenProvider.activePublicKey()

        ServerResponse
            .ok()
            .body(publicKey)
    }
    POST("current-user") {
        val userRequest = it.body<CurrentUserRequest>()
        val currentUser = userService.currentUser(userRequest)

        ServerResponse
            .ok()
            .body(currentUser)
    }

    onError<EntityNotFoundException> { _, _ ->
        ServerResponse
            .notFound()
            .build()
    }
})
