package com.github.arhor.aws.graphql.federation.users.service.event.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.event.UserEventProcessor
import com.github.arhor.aws.graphql.federation.users.service.event.UserEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Trace
@Component
class UserEventProcessorImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxEventPublisher: UserEventPublisher,
) : UserEventProcessor {

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processUserCreatedEvents() {
        dequeueAndPublishEvents<UserEvent.Created>(UserEvent.Type.USER_EVENT_CREATED)
    }

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processUserDeletedEvents() {
        dequeueAndPublishEvents<UserEvent.Deleted>(UserEvent.Type.USER_EVENT_DELETED)
    }

    private inline fun <reified T : UserEvent> dequeueAndPublishEvents(eventType: UserEvent.Type) {
        val outboxMessages =
            outboxMessageRepository.dequeueOldest(
                messageType = eventType.code,
                messagesNum = DEFAULT_EVENTS_BATCH_SIZE,
            )

        for (message in outboxMessages) {
            val event = objectMapper.convertValue<T>(message.data)

            outboxEventPublisher.publish(event, message.traceId)
        }
    }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 50
    }
}
