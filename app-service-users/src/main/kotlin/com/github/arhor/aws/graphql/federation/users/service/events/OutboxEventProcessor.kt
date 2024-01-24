package com.github.arhor.aws.graphql.federation.users.service.events

interface OutboxEventProcessor {

    fun processOutboxEvents()
}
