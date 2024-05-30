package com.github.arhor.aws.graphql.federation.common.exception

class EntityOperationRestrictedException @JvmOverloads constructor(
    entity: String,
    condition: String,
    operation: Operation,
    cause: Exception? = null,
) : EntityConditionException(entity, condition, operation, cause)
