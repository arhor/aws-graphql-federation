package com.github.arhor.aws.graphql.federation.common.event

sealed interface PostEvent : DomainEvent {

    data class Created(val id: Long) : PostEvent {
        override fun type(): String = POST_EVENT_CREATED
    }

    data class Deleted(val id: Long) : PostEvent {
        override fun type(): String = POST_EVENT_DELETED
    }

    companion object {
        const val POST_EVENT_CREATED = "PostEvent::Created"
        const val POST_EVENT_DELETED = "PostEvent::Deleted"
    }
}