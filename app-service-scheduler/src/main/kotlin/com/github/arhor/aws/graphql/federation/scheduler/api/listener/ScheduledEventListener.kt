package com.github.arhor.aws.graphql.federation.scheduler.api.listener

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID
import com.github.arhor.aws.graphql.federation.common.event.ScheduledEvent
import com.github.arhor.aws.graphql.federation.scheduler.service.ScheduledEventService
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

    @SqsListener("\${app-props.events.source.handle-scheduled-event}")
    fun handleScheduledEvent(@Payload event: ScheduledEvent, @Header(ATTR_TRACE_ID) traceId: UUID) {
        withExtendedMDC(traceId) {
            when (event) {
                is ScheduledEvent.Created -> scheduledEventService.storeScheduledEvent(event)
                is ScheduledEvent.Deleted -> scheduledEventService.clearScheduledEvent(event)
                else -> throw IllegalArgumentException("Unsupported scheduled event type: ${event.type()}")
            }
        }
    }
}
