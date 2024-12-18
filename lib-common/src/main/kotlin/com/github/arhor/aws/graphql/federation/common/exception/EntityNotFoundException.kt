package com.github.arhor.aws.graphql.federation.common.exception

// TODO: improve it to contain dynamic map of parameters instead of hardcoded properties
class EntityNotFoundException @JvmOverloads constructor(
    entity: String,
    condition: String,
    operation: Operation = Operation.UNKNOWN,
    cause: Exception? = null,
) : EntityConditionException(entity, condition, operation, cause)
