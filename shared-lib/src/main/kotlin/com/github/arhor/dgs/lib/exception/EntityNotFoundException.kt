package com.github.arhor.dgs.lib.exception

class EntityNotFoundException(
    entity: String,
    condition: String,
    operation: Operation? = null,
    cause: Exception? = null,
) : EntityConditionException(entity, condition, operation, cause)
