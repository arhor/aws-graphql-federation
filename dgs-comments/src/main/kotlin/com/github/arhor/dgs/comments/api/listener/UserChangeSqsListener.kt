package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.CommentService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserChangeSqsListener @Autowired constructor(
    private val commentService: CommentService,
) {

    @SqsListener("\${app-props.aws.sqs.user-updated-events-source}")
    fun handleUserUpdatedEvent(message: Message<Map<String, Any?>>) {
        logger.debug("Processing user-updated event: {}", message)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events-source}")
    fun handleUserDeletedEvent(message: Message<Map<String, Any?>>) {
        logger.debug("Processing user-deleted event: {}", message)

        val deletedUserId = message.payload["id"].toString().toLong()
        val affectedComments = commentService.unlinkCommentsFromUser(userId = deletedUserId)

        logger.debug("Successfully unlinked {} comments for the user with id {}", affectedComments, deletedUserId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserChangeSqsListener::class.java)
    }
}
