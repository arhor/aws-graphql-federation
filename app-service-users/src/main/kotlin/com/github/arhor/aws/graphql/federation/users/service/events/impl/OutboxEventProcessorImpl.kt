package com.github.arhor.aws.graphql.federation.users.service.events.impl

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxEventRepository
import com.github.arhor.aws.graphql.federation.users.service.events.OutboxEventProcessor
import com.github.arhor.aws.graphql.federation.users.service.events.OutboxEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Component
class OutboxEventProcessorImpl(
    private val outboxEventRepository: OutboxEventRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
) : OutboxEventProcessor {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    @Transactional
    override fun processOutboxEvents() {
        val events =
            outboxEventRepository.dequeueOldest(
                eventType = UserEvent.USER_EVENT_DELETED,
                eventsNum = DEFAULT_EVENTS_BATCH_SIZE,
            )
        for (outboxEvent in events) {
            outboxEventPublisher.publish(outboxEvent)
            outboxEventRepository.delete(outboxEvent)
        }
    }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 10
    }
}
