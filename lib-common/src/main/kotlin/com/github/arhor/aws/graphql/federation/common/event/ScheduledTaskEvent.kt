package com.github.arhor.aws.graphql.federation.common.event

import java.time.Instant
import java.util.UUID
import kotlin.reflect.KClass

sealed interface ScheduledTaskEvent : DomainEvent {

    data class Created(val id: UUID, val timestamp: Instant, val type: String, val data: String) : ScheduledTaskEvent {
        override fun type(): String = Type.SCHEDULED_TASK_EVENT_CREATED.code
    }

    data class Deleted(val id: UUID) : ScheduledTaskEvent {
        override fun type(): String = Type.SCHEDULED_TASK_EVENT_DELETED.code
    }

    enum class Type(val code: String, val type: KClass<out ScheduledTaskEvent>) {
        SCHEDULED_TASK_EVENT_CREATED("ScheduledTaskEvent::Created", Created::class),
        SCHEDULED_TASK_EVENT_DELETED("ScheduledTaskEvent::Deleted", Deleted::class),
    }
}
