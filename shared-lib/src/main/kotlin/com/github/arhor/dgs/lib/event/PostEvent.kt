package com.github.arhor.dgs.lib.event

sealed interface PostEvent : Event {

    data class Created(val id: Long) : PostEvent {
        override fun type(): String = POST_CHANGE_CREATED
    }

    data class Deleted(val id: Long) : PostEvent {
        override fun type(): String = POST_CHANGE_DELETED
    }

    companion object {
        private const val POST_CHANGE_CREATED = "PostChange.Created"
        private const val POST_CHANGE_DELETED = "PostChange.Deleted"
    }
}