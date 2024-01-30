package com.github.arhor.aws.graphql.federation.users.service.events.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventPublisher
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventProcessor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Component
class UserEventProcessorImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxEventPublisher: UserEventPublisher,
) : UserEventProcessor {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    @Transactional
    override fun processUserDeletedEvents() {
        val outboxEvents =
            outboxMessageRepository.dequeueOldest(
                messageType = UserEvent.USER_EVENT_DELETED,
                messagesNum = DEFAULT_EVENTS_BATCH_SIZE,
            )
        val event =
            outboxEvents.map { objectMapper.convertValue<UserEvent.Deleted>(it.data) }
                .flatMap { it.ids }
                .let { UserEvent.Deleted(ids = it.toSet()) }

        outboxEventPublisher.publish(event)
        outboxMessageRepository.deleteAll(outboxEvents)
    }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 10
    }
}
