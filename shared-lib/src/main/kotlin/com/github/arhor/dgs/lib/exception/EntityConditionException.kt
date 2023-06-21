package com.github.arhor.dgs.lib.exception

/**
 * @param entity    entity name
 * @param condition condition caused the exception
 * @param operation operation during which an exception occurred
 */
abstract class EntityConditionException(
    val entity: String,
    val condition: String,
    val operation: Operation? = null,
    cause: Exception? = null,
) : RuntimeException(
    "Operation [${operation.toString()}] over the entity [$entity] failed under the condition [$condition]",
    cause,
)