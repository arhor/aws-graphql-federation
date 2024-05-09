package com.github.arhor.aws.graphql.federation.posts.service.events

import com.github.arhor.aws.graphql.federation.common.event.PostEvent

interface PostEventPublisher {

    fun publish(event: PostEvent)
}
