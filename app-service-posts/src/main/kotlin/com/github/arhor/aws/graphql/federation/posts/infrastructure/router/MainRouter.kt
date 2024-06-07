package com.github.arhor.aws.graphql.federation.posts.infrastructure.router

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.starter.core.rest.sendError
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@Component
class MainRouter : RouterFunction<ServerResponse> by router({

    /* ---------- Request Mappings ---------- */

    GET("favicon.ico") {
        status(NO_CONTENT)
            .build()
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
})
