package com.github.arhor.aws.graphql.federation.common.exception

class EntityDuplicateException(
    entity: String,
    condition: String,
    operation: Operation = Operation.UNKNOWN,
    cause: Exception? = null,
) : EntityConditionException(entity, condition, operation, cause)
