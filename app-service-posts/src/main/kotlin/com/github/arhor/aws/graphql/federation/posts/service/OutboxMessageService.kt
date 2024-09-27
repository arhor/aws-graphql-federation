package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.common.event.PostEvent

interface OutboxMessageService {
    fun storeToOutboxMessages(event: PostEvent)
    fun releaseOutboxMessages(limit: Int)
}
