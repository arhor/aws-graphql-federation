package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.CommentService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostChangeSqsListener @Autowired constructor(
    private val commentService: CommentService,
) {

    @SqsListener("\${app-props.aws.sqs.post-updates}")
    fun handlePostUpdatedEvent(event: PostChange.Updated) {
        logger.debug("Processing post-updated event: {}", event)
    }

    @SqsListener("\${app-props.aws.sqs.post-deletes}")
    fun handlePostDeletedEvent(event: PostChange.Deleted) {
        logger.debug("Processing post-deleted event: {}", event)
        commentService.deleteCommentsFromPost(postId = event.id)
    }

    sealed interface PostChange {
        data class Updated(val id: Long) : PostChange
        data class Deleted(val id: Long) : PostChange
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PostChangeSqsListener::class.java)
    }
}
