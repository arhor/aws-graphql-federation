package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.DomainEvent.Companion.HEADER_IDEMPOTENCY_ID
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserService
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
        @Header(HEADER_IDEMPOTENCY_ID) idempotencyId: UUID,
    ) {
        userService.createInternalUserRepresentation(userId = event.id, idempotencyId = idempotencyId)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events:}")
    fun handleUserDeletedEvent(
        @Payload event: UserEvent.Deleted,
        @Header(HEADER_IDEMPOTENCY_ID) idempotencyId: UUID,
    ) {
        userService.deleteInternalUserRepresentation(userId = event.id, idempotencyId = idempotencyId)
    }
}
