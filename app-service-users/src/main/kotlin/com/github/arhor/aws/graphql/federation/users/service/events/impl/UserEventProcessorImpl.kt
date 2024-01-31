package com.github.arhor.aws.graphql.federation.users.service.events.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventProcessor
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Component
class UserEventProcessorImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxEventPublisher: UserEventPublisher,
) : UserEventProcessor {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processUserDeletedEvents() {
        val outboxMessages =
            outboxMessageRepository.dequeueOldest(
                messageType = UserEvent.USER_EVENT_DELETED,
                messagesNum = DEFAULT_EVENTS_BATCH_SIZE,
            )

        if (outboxMessages.isNotEmpty()) {
            val composedEvent = outboxMessages.deserialize().compose()

            outboxEventPublisher.publish(composedEvent)
            outboxMessageRepository.deleteAll(outboxMessages)
        }
    }

    private fun Collection<OutboxMessageEntity>.deserialize(): Sequence<UserEvent.Deleted> =
        this.asSequence()
            .map { objectMapper.convertValue<UserEvent.Deleted>(it.data) }

    private fun Sequence<UserEvent.Deleted>.compose(): UserEvent.Deleted =
        this.flatMap { it.ids }
            .toSet()
            .let { UserEvent.Deleted(ids = it) }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 10
    }
}
