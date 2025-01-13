package com.github.arhor.aws.graphql.federation.common.event

import java.time.Instant
import java.util.UUID
import kotlin.reflect.KClass

sealed interface ScheduledEvent : AppEvent {

    data class Publish(val id: UUID, val type: String, val data: String) : ScheduledEvent {
        override fun type(): String = Type.SCHEDULED_EVENT_PUBLISH.code
    }

    data class Created(val id: UUID, val type: String, val data: String, val time: Instant) : ScheduledEvent {
        override fun type(): String = Type.SCHEDULED_EVENT_CREATED.code
    }

    data class Deleted(val id: UUID, val type: String) : ScheduledEvent {
        override fun type(): String = Type.SCHEDULED_EVENT_DELETED.code
    }

    enum class Type(val code: String, val type: KClass<out ScheduledEvent>) {
        SCHEDULED_EVENT_PUBLISH("ScheduledEvent::Publish", Publish::class),
        SCHEDULED_EVENT_CREATED("ScheduledEvent::Created", Created::class),
        SCHEDULED_EVENT_DELETED("ScheduledEvent::Deleted", Deleted::class),
    }
}
