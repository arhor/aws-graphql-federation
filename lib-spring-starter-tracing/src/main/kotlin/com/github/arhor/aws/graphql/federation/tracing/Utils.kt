package com.github.arhor.aws.graphql.federation.tracing

import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.util.UUID

fun useContextAttribute(attribute: Attributes): UUID {
    val attributeValue =
        RequestContextHolder
            .currentRequestAttributes()
            .getAttribute(attribute.key, RequestAttributes.SCOPE_REQUEST)
            ?: throw IllegalStateException("Attribute '${attribute.key}' has not been initialized.")

    return UUID.fromString(attributeValue.toString())
}
