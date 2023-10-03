package com.github.arhor.dgs.lib.exception

class EntityDuplicateException(
    entity: String,
    condition: String,
    operation: Operation = Operation.UNKNOWN,
    cause: Exception? = null,
) : EntityConditionException(entity, condition, operation, cause)
