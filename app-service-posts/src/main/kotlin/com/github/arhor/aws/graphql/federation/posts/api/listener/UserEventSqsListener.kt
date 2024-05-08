package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Trace
@Component
class UserEventSqsListener(
    private val userService: UserService,
) {

    @SqsListener("\${app-props.aws.sqs.user-created-events:}")
    fun handleUserCreatedEvents(message: Message<UserEvent.Created>) {
        userService.createInternalUserRepresentation(userIds = message.payload.ids)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events:}")
    fun handleUserDeletedEvents(message: Message<UserEvent.Deleted>) {
        userService.deleteInternalUserRepresentation(userIds = message.payload.ids)
    }
}
