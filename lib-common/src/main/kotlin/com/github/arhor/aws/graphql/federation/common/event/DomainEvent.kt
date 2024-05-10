package com.github.arhor.aws.graphql.federation.common.event

import java.util.UUID

interface DomainEvent {

    fun type(): String

    fun attributes(idempotencyKey: UUID, vararg values: Pair<String, Any> = emptyArray()): Map<String, Any> {
        return mapOf(
            HEADER_PAYLOAD_TYPE to type(),
            HEADER_IDEMPOTENCY_KEY to idempotencyKey.toString(),
            *values
        )
    }

    companion object {
        // @formatter:off
        const val HEADER_PAYLOAD_TYPE    = "x_event_type"
        const val HEADER_IDEMPOTENCY_KEY = "x_idempotency_key"
        // @formatter:on
    }
}