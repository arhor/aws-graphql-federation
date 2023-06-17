package com.github.arhor.dgs.lib.exception

import com.netflix.graphql.dgs.exceptions.DgsException
import com.netflix.graphql.types.errors.ErrorDetail
import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.ResultPath

abstract class CustomGQLException(
    message: String,
    cause: Exception? = null,
    private val errorDetail: ErrorDetail = ErrorDetail.Common.SERVICE_ERROR
) : RuntimeException(message, cause) {

    fun toGraphQlError(path: ResultPath? = null): TypedGraphQLError =
        TypedGraphQLError
            .newBuilder()
            .apply { if (path != null) path(path) }
            .errorDetail(errorDetail)
            .message(message)
            .extensions(mapOf(DgsException.EXTENSION_CLASS_KEY to this::class.java.name))
            .build()
}