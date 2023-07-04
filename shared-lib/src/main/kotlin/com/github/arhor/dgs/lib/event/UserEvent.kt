package com.github.arhor.dgs.lib.event

sealed interface UserEvent : Event {

    data class Created(val id: Long) : UserEvent {
        override fun type(): String = USER_EVENT_CREATED
    }

    data class Deleted(val id: Long) : UserEvent {
        override fun type(): String = USER_EVENT_DELETED
    }

    companion object {
        private const val USER_EVENT_CREATED = "UserEvent.Created"
        private const val USER_EVENT_DELETED = "UserEvent.Deleted"
    }
}