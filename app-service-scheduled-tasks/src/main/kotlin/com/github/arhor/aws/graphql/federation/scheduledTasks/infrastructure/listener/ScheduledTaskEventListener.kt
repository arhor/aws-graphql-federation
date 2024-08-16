package com.github.arhor.aws.graphql.federation.scheduledTasks.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.ScheduledTaskEvent
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
class ScheduledTaskEventListener {

    @SqsListener("\${app-props.events.source.create-scheduled-task-events}")
    fun syncPostsOnUserCreatedEvent(
        @Payload event: ScheduledTaskEvent.Created,
        @Header(TRACING_ID_KEY) traceId: UUID,
        @Header(IDEMPOTENT_KEY) idempotencyKey: UUID,
    ) {
        withExtendedMDC(traceId) {
            TODO("Implement!")
        }
    }

    @SqsListener("\${app-props.events.source.delete-scheduled-task-events}")
    fun syncPostsOnUserDeletedEvent(
        @Payload event: ScheduledTaskEvent.Deleted,
        @Header(TRACING_ID_KEY) traceId: UUID,
        @Header(IDEMPOTENT_KEY) idempotencyKey: UUID,
    ) {
        withExtendedMDC(traceId) {
            TODO("Implement!")
        }
    }
}
