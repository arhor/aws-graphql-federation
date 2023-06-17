package com.github.arhor.dgs.lib.exception

import com.netflix.graphql.types.errors.ErrorDetail

/**
 * @param entity    entity name
 * @param condition condition caused the exception
 * @param operation operation during which an exception occurred
 */
abstract class EntityConditionException(
    entity: String,
    condition: String,
    operation: Operation? = null,
    cause: Exception? = null,
    errorDetail: ErrorDetail,
) : CustomGQLException(
    message = "Operation [${operation.toString()}] over the entity [$entity] failed under the condition [$condition]",
    cause = cause,
    errorDetail = errorDetail,
)