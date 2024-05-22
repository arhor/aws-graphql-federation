package com.github.arhor.aws.graphql.federation.posts.service.event.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.posts.service.event.PostEventProcessor
import com.github.arhor.aws.graphql.federation.posts.service.event.PostEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class PostEventProcessorImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxEventPublisher: PostEventPublisher,
) : PostEventProcessor {

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processPostCreatedEvents() {
        dequeueAndPublishEvents(PostEvent.Type.POST_EVENT_CREATED)
    }

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processPostDeletedEvents() {
        dequeueAndPublishEvents(PostEvent.Type.POST_EVENT_DELETED)
    }

    private fun dequeueAndPublishEvents(eventType: PostEvent.Type) {
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
