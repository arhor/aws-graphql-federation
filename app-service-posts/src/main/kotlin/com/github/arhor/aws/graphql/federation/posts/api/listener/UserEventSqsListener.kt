package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Trace
@Component
class UserEventSqsListener(
    private val postService: PostService,
) {

    @SqsListener("\${app-props.aws.sqs.user-deleted-events}")
    fun handleUserDeletedEvents(message: Message<UserEvent.Deleted>) {
        postService.unlinkPostsFromUsers(userIds = message.payload.ids)
    }
}
