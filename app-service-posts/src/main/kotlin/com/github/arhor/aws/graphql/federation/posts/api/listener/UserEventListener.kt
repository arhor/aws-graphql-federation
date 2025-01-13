package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
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

    @SqsListener("\${app-props.events.source.sync-posts-on-user-created-event}")
    fun syncPostsOnUserCreatedEvent(
        @Payload event: UserEvent.Created,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            userRepresentationService.createUserRepresentation(
                userId = event.id,
            )
        }
    }

    @SqsListener("\${app-props.events.source.sync-posts-on-user-deleted-event}")
    fun syncPostsOnUserDeletedEvent(
        @Payload event: UserEvent.Deleted,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            userRepresentationService.deleteUserRepresentation(
                userId = event.id,
            )
        }
    }
}
