package com.github.arhor.dgs.lib.event

sealed interface PostEvent : Event {

    data class Deleted(val id: Long) : PostEvent {
        override fun type(): String = POST_EVENT_DELETED
    }

    companion object {
        private const val POST_EVENT_DELETED = "PostEvent.Deleted"
    }
}