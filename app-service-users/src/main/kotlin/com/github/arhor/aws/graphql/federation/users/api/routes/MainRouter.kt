package com.github.arhor.aws.graphql.federation.users.api.routes

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router

@Configuration
class RouterConfig {

    @Bean
    fun mainRouter(userService: UserService): RouterFunction<ServerResponse> = router {
        GET("favicon.ico") {
            status(NO_CONTENT)
                .build()
        }
        "/api".nest {
            POST("/users/verify") {
                val userRequest = it.body<CurrentUserRequest>()
                val currentUser = userService.verifyUser(userRequest)

                status(OK)
                    .body(currentUser)
            }
        }
    }
}


@Component
class MainRouter(
    private val userService: UserService,
) : RouterFunction<ServerResponse> by router({

    /* ---------- Request Mappings ---------- */

    GET("favicon.ico") {
        status(NO_CONTENT)
            .build()
    }
    "/api".nest {
        POST("/users/verify") {
            val userRequest = it.body<CurrentUserRequest>()
            val currentUser = userService.verifyUser(userRequest)

            status(OK)
                .body(currentUser)
        }
    }

    /* ---------- Exception Handlers ---------- */

    onError<EntityNotFoundException> { _, _ ->
        status(NOT_FOUND)
            .build()
    }
    onError<EntityDuplicateException> { _, _ ->
        status(CONFLICT)
            .build()
    }
    onError<DgsBadRequestException> { _, _ ->
        status(BAD_REQUEST)
            .build()
    }
})
