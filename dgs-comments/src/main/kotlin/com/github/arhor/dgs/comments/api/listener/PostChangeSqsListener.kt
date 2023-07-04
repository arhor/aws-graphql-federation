package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.PostService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostChangeSqsListener @Autowired constructor(
    private val postService: PostService,
) {

    @SqsListener("\${app-props.aws.sqs.post-created-events}")
    fun handlePostCreatedEvent(event: PostEvent.Created) {
        logger.debug("Processing post-created event: {}", event)
        postService.createPost(postId = event.id)
    }

    @SqsListener("\${app-props.aws.sqs.post-deleted-events}")
    fun handlePostDeletedEvent(event: PostEvent.Deleted) {
        logger.debug("Processing post-deleted event: {}", event)
        postService.deletePost(postId = event.id)
    }

    sealed interface PostEvent {
        data class Created(val id: Long) : PostEvent
        data class Deleted(val id: Long) : PostEvent
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PostChangeSqsListener::class.java)
    }
}
