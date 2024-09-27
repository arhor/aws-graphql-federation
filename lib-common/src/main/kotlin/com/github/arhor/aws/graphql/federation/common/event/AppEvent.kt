package com.github.arhor.aws.graphql.federation.common.event

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_EVENT_TYPE
import com.github.arhor.aws.graphql.federation.common.constants.ATTR_IDEMPOTENCY_KEY
import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID

private typealias Attrs = Pair<String, String>

interface AppEvent {

    fun type(): String

    fun attributes(traceId: String, idempotencyKey: String, vararg rest: Attrs): Map<String, String> {
        return attributes(type(), traceId, idempotencyKey, *rest)
    }

    companion object {
        fun attributes(type: String, traceId: String, idempotencyKey: String, vararg rest: Attrs): Map<String, String> {
            return mapOf(
                ATTR_TRACE_ID to traceId,
                ATTR_EVENT_TYPE to type,
                ATTR_IDEMPOTENCY_KEY to idempotencyKey,
                *rest
            )
        }
    }
}
