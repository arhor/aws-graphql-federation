package com.github.arhor.aws.graphql.federation.common.exception

import com.github.arhor.aws.graphql.federation.common.toSet

/**
 * Any Exception related to the operation with domain entity.
 *
 * @param entity    entity name
 * @param condition condition caused the exception
 * @param operation operation during which an exception occurred
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class EntityConditionException(
    val entity: String,
    val condition: String,
    val operation: Operation,
    cause: Exception? = null,
) : RuntimeException(
    /* message = */ "Operation [${operation}] for the entity [$entity] failed under the condition [$condition]",
    /* cause   = */ cause,
) {
    /**
     * Filling exception stacktrace is disabled due to performance reasons,
     * also having in mind that domain entity exception is not an exceptional case,
     * but indication to return error response.
     */
    override fun fillInStackTrace(): Throwable {
        val activeProfiles = System.getProperties()
            ?.getProperty("spring.profiles.active")
            ?.takeIf { it.isNotBlank() }
            ?.lowercase()
            ?.split(",")
            ?.toSet { it.trim() }

        if (activeProfiles != null && ("test" in activeProfiles || "dev" in activeProfiles)) {
            return super.fillInStackTrace()
        }
        return this
    }
}
