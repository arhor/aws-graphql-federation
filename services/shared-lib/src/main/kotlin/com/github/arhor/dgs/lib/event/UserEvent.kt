package com.github.arhor.dgs.lib.event

sealed interface UserEvent : DomainEvent {

    data class Deleted(val id: Long) : UserEvent {
        override fun type(): String = USER_EVENT_DELETED
    }

    companion object {
        private const val USER_EVENT_DELETED = "UserEvent::Deleted"
    }
}