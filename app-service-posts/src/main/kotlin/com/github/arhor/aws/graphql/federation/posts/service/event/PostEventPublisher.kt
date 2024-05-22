package com.github.arhor.aws.graphql.federation.posts.service.event

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import java.util.UUID

interface PostEventPublisher {

    fun publish(event: PostEvent, traceId: UUID, idempotentKey: UUID)
}
