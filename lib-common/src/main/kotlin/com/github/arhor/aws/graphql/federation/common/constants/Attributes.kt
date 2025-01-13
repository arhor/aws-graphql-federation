package com.github.arhor.aws.graphql.federation.common.constants

import java.util.UUID

private fun randomUUID() = UUID.randomUUID().toString()

// @formatter:off
const val ATTR_TRACE_ID        = "x-trace-id"
const val ATTR_REQUEST_ID      = "x-request-id"
const val ATTR_EVENT_TYPE      = "x-event-type"
// @formatter:on

enum class Attributes(val key: String, val default: (() -> String)? = null) {
    // @formatter:off
    TRACE_ID   (key = ATTR_TRACE_ID  , default = ::randomUUID),
    REQUEST_ID (key = ATTR_REQUEST_ID, default = ::randomUUID),
    EVENT_TYPE (key = ATTR_EVENT_TYPE),
    // @formatter:on
    ;

    fun defaultValue() = default?.invoke()
}
