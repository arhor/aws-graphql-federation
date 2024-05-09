package com.github.arhor.aws.graphql.federation.posts.service.events.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.posts.service.events.PostEventProcessor
import com.github.arhor.aws.graphql.federation.posts.service.events.PostEventPublisher
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

    @Scheduled(cron = "\${app-props.outbox-events-processing-cron:}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processPostCreatedEvents() {
        dequeueAndPublishEvents(
            eventType = PostEvent.Type.POST_EVENT_CREATED,
            composeFn = ::composeCreatedEvents
        )
    }

    @Scheduled(cron = "\${app-props.outbox-events-processing-cron:}")
    @Transactional(propagation = Propagation.REQUIRED)
    override fun processPostDeletedEvents() {
        dequeueAndPublishEvents(
            eventType = PostEvent.Type.POST_EVENT_DELETED,
            composeFn = ::composeDeletedEvents
        )
    }

    private inline fun <reified T : PostEvent> dequeueAndPublishEvents(
        eventType: PostEvent.Type,
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

    private inline fun <reified T : PostEvent> Collection<OutboxMessageEntity>.deserialize(): Sequence<T> =
        this.asSequence()
            .map { objectMapper.convertValue(it.data) }

    private fun composeDeletedEvents(data: Sequence<PostEvent.Deleted>): PostEvent.Deleted =
        data.flatMap { it.ids }
            .toSet()
            .let { PostEvent.Deleted(ids = it) }

    private fun composeCreatedEvents(data: Sequence<PostEvent.Created>): PostEvent.Created =
        data.flatMap { it.ids }
            .toSet()
            .let { PostEvent.Created(ids = it) }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 50
    }
}
