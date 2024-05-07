package com.github.arhor.aws.graphql.federation.common.event

sealed interface UserEvent : DomainEvent {

    data class Created(val ids: Set<Long>) : UserEvent {
        override fun type(): String = USER_EVENT_CREATED
    }

    data class Deleted(val ids: Set<Long>) : UserEvent {
        override fun type(): String = USER_EVENT_DELETED
    }

    companion object {
        const val USER_EVENT_CREATED = "UserEvent::Created"
        const val USER_EVENT_DELETED = "UserEvent::Deleted"
    }
}