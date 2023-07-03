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

    init {
        logger.info(">>>>> $javaClass initialized! <<<<<")
    }

    @SqsListener("\${app-props.aws.sqs.user-updates}")
    fun handleUserUpdatedEvent(event: Message<String>) {
        logger.debug("Processing user-updated event: {}", event)
    }

    @SqsListener("\${app-props.aws.sqs.user-deletes}")
    fun handleUserDeletedEvent(event: Message<String>) {
        logger.debug("Processing user-deleted event: {}", event)
        commentService.unlinkCommentsFromUser(userId = 1L /*event.id*/)
    }

    sealed interface UserChange {
        data class Updated(val id: Long) : UserChange
        data class Deleted(val id: Long) : UserChange
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserChangeSqsListener::class.java)
    }
}
