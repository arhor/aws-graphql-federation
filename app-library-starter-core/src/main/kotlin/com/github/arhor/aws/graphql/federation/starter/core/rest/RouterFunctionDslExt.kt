package com.github.arhor.aws.graphql.federation.starter.core.rest

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunctionDsl

fun RouterFunctionDsl.sendError(status: HttpStatus, message: String?) =
    status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(ApiError(message = message))
