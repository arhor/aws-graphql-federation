package com.github.arhor.aws.graphql.federation.common.event

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_EVENT_TYPE
import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID

private typealias Attrs = Pair<String, String>

interface AppEvent {

    fun type(): String

    fun attributes(traceId: String, vararg rest: Attrs): Map<String, String> {
        return attributes(type(), traceId, *rest)
    }

    companion object {
        fun attributes(type: String, traceId: String, vararg rest: Attrs): Map<String, String> {
            return mapOf(
                ATTR_TRACE_ID to traceId,
                ATTR_EVENT_TYPE to type,
                *rest
            )
        }
    }
}
