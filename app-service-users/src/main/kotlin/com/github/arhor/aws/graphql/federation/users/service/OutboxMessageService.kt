package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.common.event.UserEvent

interface OutboxMessageService {
    fun storeAsOutboxMessage(event: UserEvent)
    fun releaseOutboxMessagesOfType(eventType: UserEvent.Type)
}
