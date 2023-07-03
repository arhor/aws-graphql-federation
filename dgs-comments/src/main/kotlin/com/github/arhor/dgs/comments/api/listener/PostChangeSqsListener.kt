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

    init {
        logger.info(">>>>> $javaClass initialized! <<<<<")
    }

    @SqsListener("\${app-props.aws.sqs.post-updates}")
    fun handlePostUpdatedEvent(event: Message<PostChange.Updated>) {
        logger.debug("Processing post-updated event: {}", event)
    }

    @SqsListener("\${app-props.aws.sqs.post-deletes}")
    fun handlePostDeletedEvent(event: Message<PostChange.Deleted>) {
        logger.debug("Processing post-deleted event: {}", event)
        commentService.deleteCommentsFromPost(postId = 1L /*event.id*/)
    }

    sealed interface PostChange {
        data class Updated(val id: Long) : PostChange
        data class Deleted(val id: Long) : PostChange
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PostChangeSqsListener::class.java)
    }
}
