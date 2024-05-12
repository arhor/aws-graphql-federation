package com.github.arhor.aws.graphql.federation.common.event

interface DomainEvent {

    fun type(): String

    fun attributes(vararg values: Pair<String, Any> = emptyArray()): Map<String, Any> {
        return mapOf(ATTRS_PAYLOAD_TYPE to type(), *values)
    }

    companion object {
        const val ATTRS_PAYLOAD_TYPE = "x-event-type"
    }
}