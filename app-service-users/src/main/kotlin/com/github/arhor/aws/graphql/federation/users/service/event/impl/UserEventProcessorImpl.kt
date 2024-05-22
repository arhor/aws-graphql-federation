package com.github.arhor.aws.graphql.federation.users.service.event.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.event.UserEventProcessor
import com.github.arhor.aws.graphql.federation.users.service.event.UserEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class UserEventProcessorImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxEventPublisher: UserEventPublisher,
) : UserEventProcessor {

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processUserCreatedEvents() {
        dequeueAndPublishEvents(UserEvent.Type.USER_EVENT_CREATED)
    }

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processUserDeletedEvents() {
        dequeueAndPublishEvents(UserEvent.Type.USER_EVENT_DELETED)
    }

    private fun dequeueAndPublishEvents(eventType: UserEvent.Type) {
        val outboxMessages =
            outboxMessageRepository.dequeueOldest(
                messageType = eventType.code,
                messagesNum = DEFAULT_EVENTS_BATCH_SIZE,
            )

        for (message in outboxMessages) {
            val event = objectMapper.convertValue(message.data, eventType.type.java)

            outboxEventPublisher.publish(
                event = event,
                traceId = message.traceId,
                idempotentKey = message.id!!,
            )
        }
    }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 50
    }
}
