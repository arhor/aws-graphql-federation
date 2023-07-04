package com.github.arhor.dgs.lib.event

interface Event {

    fun type(): String

    fun attributes(): Map<String, Any> {
        return mapOf(HEADER_PAYLOAD_TYPE to type())
    }

    companion object {
        const val HEADER_PAYLOAD_TYPE = "x_event_type"
    }
}