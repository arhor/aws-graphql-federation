package com.github.arhor.aws.graphql.federation.common.event

import java.time.Instant
import java.util.UUID
import kotlin.reflect.KClass

sealed interface ScheduledEvent : AppEvent {

    data class Created(val id: UUID, val timestamp: Instant, val type: String, val data: String) : ScheduledEvent {
        override fun type(): String = Type.SCHEDULED_EVENT_CREATED.code
    }

    data class Deleted(val id: UUID) : ScheduledEvent {
        override fun type(): String = Type.SCHEDULED_EVENT_DELETED.code
    }

    enum class Type(val code: String, val type: KClass<out ScheduledEvent>) {
        SCHEDULED_EVENT_CREATED("ScheduledEvent::Created", Created::class),
        SCHEDULED_EVENT_DELETED("ScheduledEvent::Deleted", Deleted::class),
    }
}
