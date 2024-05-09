package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Component

@Trace
@Component
class UserEventListener(
    private val userService: UserService,
) {

    @SqsListener("\${app-props.aws.sqs.user-created-events:}")
    fun handleUserCreatedEvent(event: UserEvent.Created) {
        userService.createInternalUserRepresentation(userIds = event.ids)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events:}")
    fun handleUserDeletedEvent(event: UserEvent.Deleted) {
        userService.deleteInternalUserRepresentation(userIds = event.ids)
    }
}
