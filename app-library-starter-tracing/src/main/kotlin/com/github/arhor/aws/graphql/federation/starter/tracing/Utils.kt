package com.github.arhor.aws.graphql.federation.starter.tracing

import com.github.arhor.aws.graphql.federation.common.constants.Attributes
import org.slf4j.MDC
import java.util.UUID

fun useContextAttribute(attribute: Attributes): UUID {
    val attributeValue =
        MDC.get(attribute.key)
            ?: throw IllegalStateException("Attribute '${attribute.key}' has not been initialized.")

    return UUID.fromString(attributeValue.toString())
}

inline fun <T> withExtendedMDC(traceId: UUID, block: () -> T): T {
    val attributeKey = Attributes.TRACE_ID.key
    MDC.put(attributeKey, traceId.toString())
    try {
        return block()
    } finally {
        MDC.remove(attributeKey)
    }
}
