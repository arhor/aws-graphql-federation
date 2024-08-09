package com.github.arhor.aws.graphql.federation.posts.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.starter.tracing.withExtendedMDC
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.UUID

@Trace
@Component
class UserEventListener(
    private val userRepresentationService: UserRepresentationService,
) {

    @SqsListener("\${${AppProps.Aws.Sqs.SYNC_POSTS_ON_USER_CREATED_EVENT}}")
    fun syncPostsOnUserCreatedEvent(
        @Payload event: UserEvent.Created,
        @Header(TRACING_ID_KEY) traceId: UUID,
        @Header(IDEMPOTENT_KEY) idempotencyKey: UUID,
    ) {
        withExtendedMDC(traceId) {
            userRepresentationService.createUserRepresentation(
                userId = event.id,
                idempotencyKey = idempotencyKey,
            )
        }
    }

    @SqsListener("\${${AppProps.Aws.Sqs.SYNC_POSTS_ON_USER_DELETED_EVENT}}")
    fun syncPostsOnUserDeletedEvent(
        @Payload event: UserEvent.Deleted,
        @Header(TRACING_ID_KEY) traceId: UUID,
        @Header(IDEMPOTENT_KEY) idempotencyKey: UUID,
    ) {
        withExtendedMDC(traceId) {
            userRepresentationService.deleteUserRepresentation(
                userId = event.id,
                idempotencyKey = idempotencyKey,
            )
        }
    }
}
