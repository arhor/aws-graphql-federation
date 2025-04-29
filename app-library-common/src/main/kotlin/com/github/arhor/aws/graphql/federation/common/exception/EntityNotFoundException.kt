package com.github.arhor.aws.graphql.federation.common.exception

open class EntityNotFoundException : EntityConditionException {

    @JvmOverloads
    constructor(
        entity: String,
        condition: String,
        operation: Operation = Operation.UNKNOWN,
        cause: Exception? = null
    ) : super(entity, condition, operation, cause)

    @JvmOverloads
    constructor(
        entity: String,
        condition: Map<String, Any?>,
        operation: Operation = Operation.UNKNOWN,
        cause: Exception? = null
    ) : super(entity, condition, operation, cause)
}
