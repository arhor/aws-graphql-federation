package com.github.arhor.aws.graphql.federation.votes.api.listener

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.starter.tracing.Utils.withExtendedMDC
import com.github.arhor.aws.graphql.federation.votes.service.PostRepresentationService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.UUID

@Trace
@Component
class PostEventListener(
    private val postService: PostRepresentationService,
) {

    @SqsListener("\${app-props.events.source.sync-votes-on-post-created-event}")
    fun syncVotesOnPostCreatedEvent(
        @Payload event: PostEvent.Created,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            postService.createPostRepresentation(
                postId = event.id,
                userId = event.userId,
                idempotencyKey = traceId,
            )
        }
    }

    @SqsListener("\${app-props.events.source.sync-votes-on-post-deleted-event}")
    fun syncVotesOnPostDeletedEvent(
        @Payload event: PostEvent.Deleted,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            postService.deletePostRepresentation(
                postId = event.id,
                idempotencyKey = traceId,
            )
        }
    }
}
