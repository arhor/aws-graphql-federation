package com.github.arhor.aws.graphql.federation.users.api.router

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.starter.core.rest.sendError
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.users.service.UserService
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router

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
        POST("/users/authenticate") {
            val userRequest = it.body<CurrentUserRequest>()
            val currentUser = userService.getUserByUsernameAndPassword(userRequest)

            status(OK)
                .body(currentUser)
        }
    }

    /* ---------- Exception Handlers ---------- */

    onError<EntityNotFoundException> { e, _ ->
        sendError(
            status = NOT_FOUND,
            message = e.message
        )
    }
    onError<EntityDuplicateException> { e, _ ->
        sendError(
            status = CONFLICT,
            message = e.message
        )
    }
    onError<UsernameNotFoundException> { e, _ ->
        sendError(
            status = BAD_REQUEST,
            message = e.message
        )
    }
})
