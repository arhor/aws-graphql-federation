package com.github.arhor.aws.graphql.federation.common.event

import java.util.UUID
import kotlin.reflect.KClass

sealed interface CommentEvent : AppEvent {

    data class Created(val id: UUID, val userId: UUID, val postId: UUID) : CommentEvent {
        override fun type(): String = Type.COMMENT_EVENT_CREATED.code
    }

    data class Deleted(val id: UUID) : CommentEvent {
        override fun type(): String = Type.COMMENT_EVENT_DELETED.code
    }

    enum class Type(val code: String, val type: KClass<out CommentEvent>) {
        COMMENT_EVENT_CREATED("CommentEvent::Created", Created::class),
        COMMENT_EVENT_DELETED("CommentEvent::Deleted", Deleted::class),
    }
}
