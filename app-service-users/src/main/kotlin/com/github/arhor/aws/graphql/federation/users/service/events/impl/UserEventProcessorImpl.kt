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

@Component
class UserEventProcessorImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxEventPublisher: UserEventPublisher,
) : UserEventProcessor {

    @Scheduled(cron = "\${app-props.outbox-events-processing-cron:}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processUserCreatedEvents() {
        dequeueAndPublishEvents(
            eventType = UserEvent.Type.USER_EVENT_CREATED,
            composeFn = ::composeCreatedEvents
        )
    }

    @Scheduled(cron = "\${app-props.outbox-events-processing-cron:}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processUserDeletedEvents() {
        dequeueAndPublishEvents(
            eventType = UserEvent.Type.USER_EVENT_DELETED,
            composeFn = ::composeDeletedEvents
        )
    }

    private inline fun <reified T : UserEvent> dequeueAndPublishEvents(
        eventType: UserEvent.Type,
        composeFn: Sequence<T>.() -> T,
    ) {
        val outboxMessages =
            outboxMessageRepository.dequeueOldest(
                messageType = eventType.code,
                messagesNum = DEFAULT_EVENTS_BATCH_SIZE,
            )

        if (outboxMessages.isNotEmpty()) {
            val composedEvent = outboxMessages.deserialize<T>().composeFn()

            outboxEventPublisher.publish(composedEvent)
            outboxMessageRepository.deleteAll(outboxMessages)
        }
    }

    private inline fun <reified T : UserEvent> Collection<OutboxMessageEntity>.deserialize(): Sequence<T> =
        this.asSequence()
            .map { objectMapper.convertValue(it.data) }

    private fun composeDeletedEvents(data: Sequence<UserEvent.Deleted>): UserEvent.Deleted =
        data.flatMap { it.ids }
            .toSet()
            .let { UserEvent.Deleted(ids = it) }

    private fun composeCreatedEvents(data: Sequence<UserEvent.Created>): UserEvent.Created =
        data.flatMap { it.ids }
            .toSet()
            .let { UserEvent.Created(ids = it) }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 50
    }
}
