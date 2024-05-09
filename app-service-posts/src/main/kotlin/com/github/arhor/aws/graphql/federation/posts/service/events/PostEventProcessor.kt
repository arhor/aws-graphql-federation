package com.github.arhor.aws.graphql.federation.posts.service.events

interface PostEventProcessor {

    fun processPostDeletedEvents()
    fun processPostCreatedEvents()
}
