package com.github.arhor.dgs.posts.service.events

import com.github.arhor.dgs.lib.event.PostEvent

interface PostEventEmitter {

    fun emit(event: PostEvent)
}
