package com.github.arhor.dgs.posts.service.events

import com.github.arhor.aws.graphql.federation.common.event.PostEvent

interface PostEventEmitter {

    fun emit(event: PostEvent)
}
