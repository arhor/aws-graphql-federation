package com.github.arhor.aws.graphql.federation.tracing

import java.util.UUID

fun <R> withTracingContext(
    source: (String) -> String?,
    target: (String, String) -> Unit,
    block: () -> R,
): R {
    val tracingId = source(TRACING_ID_HEADER).generateIfMissing()
    val requestId = source(REQUEST_ID_HEADER).generateIfMissing()

    return try {
        block()
    } finally {

    }
}

private fun String?.generateIfMissing() = this?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
