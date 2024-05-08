package com.github.arhor.aws.graphql.federation.users.service.events

interface UserEventProcessor {

    fun processUserDeletedEvents()
    fun processUserCreatedEvents()
}
