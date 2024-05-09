package com.github.arhor.aws.graphql.federation.common.event

import java.util.UUID

sealed interface PostEvent : DomainEvent {

    data class Created(val ids: Set<UUID>) : PostEvent {

        constructor(id: UUID) : this(ids = setOf(id))

        override fun type(): String = Type.POST_EVENT_CREATED.code
    }

    data class Deleted(val ids: Set<UUID>) : PostEvent {

        constructor(id: UUID) : this(ids = setOf(id))

        override fun type(): String = Type.POST_EVENT_DELETED.code
    }

    enum class Type(val code: String) {
        POST_EVENT_CREATED("PostEvent::Created"),
        POST_EVENT_DELETED("PostEvent::Deleted"),
    }
}