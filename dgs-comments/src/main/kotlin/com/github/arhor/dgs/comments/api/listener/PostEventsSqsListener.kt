package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.PostService
import com.github.arhor.dgs.lib.event.PostEvent
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostEventsSqsListener @Autowired constructor(
    private val postService: PostService,
) {

    @SqsListener("\${app-props.aws.sqs.post-created-events}")
    fun handlePostCreatedEvent(event: PostEvent.Created) {
        postService.createPost(postId = event.id)
    }

    @SqsListener("\${app-props.aws.sqs.post-deleted-events}")
    fun handlePostDeletedEvent(event: PostEvent.Deleted) {
        postService.deletePost(postId = event.id)
    }
}
