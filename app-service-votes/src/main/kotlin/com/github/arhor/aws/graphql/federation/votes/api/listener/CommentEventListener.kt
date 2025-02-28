package com.github.arhor.aws.graphql.federation.votes.api.listener

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID
import com.github.arhor.aws.graphql.federation.common.event.CommentEvent
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.starter.tracing.Utils.withExtendedMDC
import com.github.arhor.aws.graphql.federation.votes.service.CommentRepresentationService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.UUID

@Trace
@Component
class CommentEventListener(
    private val commentService: CommentRepresentationService,
) {

    @SqsListener("\${app-props.events.source.sync-votes-on-comment-created-event}")
    fun syncVotesOnCommentCreatedEvent(
        @Payload event: CommentEvent.Created,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            commentService.createCommentRepresentation(
                commentId = event.id,
                postId = event.postId,
                userId = event.userId,
                idempotencyKey = traceId,
            )
        }
    }

    @SqsListener("\${app-props.events.source.sync-votes-on-comment-deleted-event}")
    fun syncVotesOnCommentDeletedEvent(
        @Payload event: CommentEvent.Deleted,
        @Header(ATTR_TRACE_ID) traceId: UUID,
    ) {
        withExtendedMDC(traceId) {
            commentService.deleteCommentRepresentation(
                commentId = event.id,
                idempotencyKey = traceId,
            )
        }
    }
}
