package com.github.arhor.aws.graphql.federation.users.service.events

import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity

interface OutboxEventPublisher {

    fun publish(outboxEvent: OutboxEventEntity)
}
