package com.github.arhor.aws.graphql.federation.common.event

import java.util.UUID
import kotlin.reflect.KClass

sealed interface PostEvent : DomainEvent {

    data class Created(val id: UUID, val userId: UUID) : PostEvent {
        override fun type(): String = Type.POST_EVENT_CREATED.code
    }

    data class Deleted(val id: UUID) : PostEvent {
        override fun type(): String = Type.POST_EVENT_DELETED.code
    }

    enum class Type(val code: String, val type: KClass<out PostEvent>) {
        POST_EVENT_CREATED("PostEvent::Created", Created::class),
        POST_EVENT_DELETED("PostEvent::Deleted", Deleted::class),
    }
}
