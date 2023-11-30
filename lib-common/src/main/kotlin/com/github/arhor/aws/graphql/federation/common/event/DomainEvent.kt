package com.github.arhor.aws.graphql.federation.common.event

interface DomainEvent {

    fun type(): String

    fun attributes(vararg values: Pair<String, Any> = emptyArray()): Map<String, Any> {
        return mapOf(HEADER_PAYLOAD_TYPE to type(), *values)
    }

    companion object {
        const val HEADER_PAYLOAD_TYPE = "x_event_type"
    }
}