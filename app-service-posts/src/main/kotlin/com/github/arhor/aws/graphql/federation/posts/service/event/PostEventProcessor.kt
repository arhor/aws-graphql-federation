package com.github.arhor.aws.graphql.federation.posts.service.event

interface PostEventProcessor {

    fun processPostDeletedEvents()
    fun processPostCreatedEvents()
}
