package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserEventSqsListener @Autowired constructor(
    private val postService: PostService,
) {

    @SqsListener("\${app-props.aws.sqs.user-deleted-events}")
    fun handleUserDeletedEvents(event: UserEvent.Deleted) {
        val deletedUserId = event.id

        logger.debug("Processing user deleted event with id: {}", deletedUserId)
        postService.unlinkPostsFromUser(userId = deletedUserId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserEventSqsListener::class.java)
    }
}
