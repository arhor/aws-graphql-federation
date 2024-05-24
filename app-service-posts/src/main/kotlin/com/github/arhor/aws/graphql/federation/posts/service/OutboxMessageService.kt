package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.common.event.PostEvent

interface OutboxMessageService {
    fun storeAsOutboxMessage(event: PostEvent)
    fun releaseOutboxMessagesOfType(eventType: PostEvent.Type)
}
