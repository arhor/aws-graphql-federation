package com.github.arhor.aws.graphql.federation.users.service.event

interface UserEventProcessor {

    fun processUserDeletedEvents()
    fun processUserCreatedEvents()
}
