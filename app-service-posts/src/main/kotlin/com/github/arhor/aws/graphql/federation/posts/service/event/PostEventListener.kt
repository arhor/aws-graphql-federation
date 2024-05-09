package com.github.arhor.aws.graphql.federation.posts.service.event

import com.github.arhor.aws.graphql.federation.common.event.PostEvent

interface PostEventListener {

    fun onPostEvent(event: PostEvent)
}
