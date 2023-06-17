package com.github.arhor.dgs.lib.exception

import com.netflix.graphql.types.errors.ErrorDetail

class EntityDuplicateException(
    entity: String,
    condition: String,
    operation: Operation? = null,
    cause: Exception? = null,
) : EntityConditionException(
    entity,
    condition,
    operation,
    cause,
    ErrorDetail.Common.CONFLICT
)
