package com.github.arhor.aws.graphql.federation.common.event

import java.util.UUID
import kotlin.reflect.KClass

sealed interface UserEvent : DomainEvent {

    data class Created(val ids: Set<UUID>) : UserEvent {

        constructor(id: UUID) : this(ids = setOf(id))

        override fun type(): String = Type.USER_EVENT_CREATED.code
    }

    data class Deleted(val ids: Set<UUID>) : UserEvent {

        constructor(id: UUID) : this(ids = setOf(id))

        override fun type(): String = Type.USER_EVENT_DELETED.code
    }

    enum class Type(val code: String, val clazz: KClass<out UserEvent>) {
        USER_EVENT_CREATED("UserEvent::Created", Created::class),
        USER_EVENT_DELETED("UserEvent::Deleted", Deleted::class),
    }
}