package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.common.event.UserEvent

interface OutboxMessageService {
    fun storeToOutboxMessages(event: UserEvent)
    fun releaseOutboxMessages(limit: Int)
}
