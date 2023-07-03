package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.CommentService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class PostChangeSqsListener @Autowired constructor(
    private val commentService: CommentService,
) {

    @SqsListener("\${app-props.aws.sqs.post-updated-events-source}")
    fun handlePostUpdatedEvent(message: Message<Map<String, Any?>>) {
        logger.debug("Processing post-updated event: {}", message)
    }

    @SqsListener("\${app-props.aws.sqs.post-deleted-events-source}")
    fun handlePostDeletedEvent(message: Message<Map<String, Any?>>) {
        logger.debug("Processing post-deleted event: {}", message)

        val deletedPostId = message.payload["id"].toString().toLong()
        val affectedComments = commentService.deleteCommentsFromPost(postId = deletedPostId)

        logger.debug("Successfully deleted {} comments for the post with id {}", affectedComments, deletedPostId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PostChangeSqsListener::class.java)
    }
}
