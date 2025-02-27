package com.github.arhor.aws.graphql.federation.votes.api.listener

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.starter.tracing.Utils.withExtendedMDC
import com.github.arhor.aws.graphql.federation.votes.service.UserRepresentationService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.*

@Trace
@Component
class UserEventListener(
    private val userService: UserRepresentationService,
) {

    @SqsListener("\${app-props.events.source.sync-votes-on-user-created-event}")
    fun syncVotesOnUserCreatedEvent(
        @Payload event: UserEvent.Created,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            userService.createUserRepresentation(
                userId = event.id,
                idempotencyKey = traceId,
            )
        }
    }

    @SqsListener("\${app-props.events.source.sync-votes-on-user-deleted-event}")
    fun syncVotesOnVotesDeletedEvent(
        @Payload event: UserEvent.Deleted,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            userService.deleteUserRepresentation(
                userId = event.id,
                idempotencyKey = traceId,
            )
        }
    }
}
