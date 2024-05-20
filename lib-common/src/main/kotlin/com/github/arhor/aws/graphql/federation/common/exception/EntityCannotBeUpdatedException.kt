package com.github.arhor.aws.graphql.federation.common.exception

class EntityCannotBeUpdatedException @JvmOverloads constructor(
    entity: String,
    condition: String,
    cause: Exception? = null,
) : EntityConditionException(entity, condition, Operation.UPDATE, cause)
