package com.github.arhor.aws.graphql.federation.common.event

import java.util.UUID

sealed interface UserEvent : DomainEvent {

    data class Created(val id: UUID) : UserEvent {
        override fun type(): String = Type.USER_EVENT_CREATED.code
    }

    data class Deleted(val id: UUID) : UserEvent {
        override fun type(): String = Type.USER_EVENT_DELETED.code
    }

    enum class Type(val code: String) {
        USER_EVENT_CREATED("UserEvent::Created"),
        USER_EVENT_DELETED("UserEvent::Deleted"),
    }
}