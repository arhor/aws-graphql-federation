package com.github.arhor.aws.graphql.federation.scheduledEvents.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.ScheduledEvent
import com.github.arhor.aws.graphql.federation.scheduledEvents.service.ScheduledEventService
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
class ScheduledEventListener(
    private val scheduledEventService: ScheduledEventService,
) {

    @SqsListener("\${app-props.events.source.create-scheduled-event}")
    fun createScheduledEvent(
        @Payload event: ScheduledEvent.Created,
        @Header(TRACING_ID_KEY) traceId: UUID,
        @Header(IDEMPOTENT_KEY) idempotencyKey: UUID,
    ) {
        withExtendedMDC(traceId) {
            scheduledEventService.storeCreatedScheduledEvent(event)
        }
    }

    @SqsListener("\${app-props.events.source.delete-scheduled-event}")
    fun deleteScheduledEvent(
        @Payload event: ScheduledEvent.Deleted,
        @Header(TRACING_ID_KEY) traceId: UUID,
        @Header(IDEMPOTENT_KEY) idempotencyKey: UUID,
    ) {
        withExtendedMDC(traceId) {
            scheduledEventService.clearCreatedScheduledEvent(event)
        }
    }
}