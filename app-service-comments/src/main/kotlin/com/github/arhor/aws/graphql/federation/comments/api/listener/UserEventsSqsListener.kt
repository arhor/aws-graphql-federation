package com.github.arhor.aws.graphql.federation.comments.api.listener

import com.github.arhor.aws.graphql.federation.comments.service.CommentService
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserEventsSqsListener @Autowired constructor(
    private val commentService: CommentService,
) {

    @SqsListener("\${app-props.aws.sqs.user-deleted-events}")
    fun handleUserDeletedEvent(event: UserEvent.Deleted) {
        val deletedUserId = event.id

        logger.debug("Processing user deleted event with id: {}", deletedUserId)
        commentService.unlinkCommentsFromUser(userId = deletedUserId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserEventsSqsListener::class.java)
    }
}
