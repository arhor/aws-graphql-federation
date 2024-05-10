package com.github.arhor.aws.graphql.federation.common.event

import java.util.UUID

interface DomainEvent {

    fun type(): String

    fun attributes(idempotencyId: UUID, vararg values: Pair<String, Any> = emptyArray()): Map<String, Any> {
        return mapOf(
            HEADER_PAYLOAD_TYPE to type(),
            HEADER_IDEMPOTENCY_ID to idempotencyId.toString(),
            *values
        )
    }

    companion object {
        // @formatter:off
        const val HEADER_PAYLOAD_TYPE   = "x_event_type"
        const val HEADER_IDEMPOTENCY_ID = "x_idempotency_id"
        // @formatter:on
    }
}