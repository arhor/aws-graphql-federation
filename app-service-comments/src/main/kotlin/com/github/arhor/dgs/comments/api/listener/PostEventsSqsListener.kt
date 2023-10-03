package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.CommentService
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostEventsSqsListener @Autowired constructor(
    private val commentService: CommentService,
) {

    @SqsListener("\${app-props.aws.sqs.post-deleted-events}")
    fun handlePostDeletedEvent(event: PostEvent.Deleted) {
        val deletedUserId = event.id

        logger.debug("Processing post deleted event with id: {}", deletedUserId)
        commentService.deleteCommentsFromPost(postId = deletedUserId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PostEventsSqsListener::class.java)
    }
}
