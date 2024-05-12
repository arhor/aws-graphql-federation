package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserService
import com.github.arhor.aws.graphql.federation.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.tracing.Trace
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.UUID

@Trace
@Component
class UserEventListener(
    private val userService: UserService,
) {

    @SqsListener("\${app-props.aws.sqs.user-created-events:}")
    fun handleUserCreatedEvent(
        @Payload event: UserEvent.Created,
        @Header(TRACING_ID_KEY) traceId: UUID,
    ) {
        userService.createInternalUserRepresentation(userId = event.id, idempotencyKey = traceId)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events:}")
    fun handleUserDeletedEvent(
        @Payload event: UserEvent.Deleted,
        @Header(TRACING_ID_KEY) traceId: UUID,
    ) {
        userService.deleteInternalUserRepresentation(userId = event.id, idempotencyKey = traceId)
    }
}
