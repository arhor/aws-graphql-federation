package com.github.arhor.aws.graphql.federation.posts.service.event

import com.github.arhor.aws.graphql.federation.common.event.PostEvent

interface PostEventPublisher {

    fun publish(event: PostEvent)
}
