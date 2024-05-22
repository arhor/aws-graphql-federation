package com.github.arhor.aws.graphql.federation.posts.service.event.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.posts.service.event.PostEventPublisher
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PostEventProcessorImplTest {

    private val objectMapper = mockk<ObjectMapper>()
    private val outboxMessageRepository = mockk<OutboxMessageRepository>()
    private val postEventPublisher = mockk<PostEventPublisher>()

    private val postEventProcessor = PostEventProcessorImpl(
        objectMapper,
        outboxMessageRepository,
        postEventPublisher,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(
            objectMapper,
            outboxMessageRepository,
            postEventPublisher,
        )
    }

    @Nested
    @DisplayName("PostEventProcessor :: processPostCreatedEvents")
    inner class ProcessPostCreatedEventsTest {
        @Test
        fun `should publish PostEvent#Created using PostEventPublisher instance`() {
            // Given
            val eventTypeCode = PostEvent.Type.POST_EVENT_CREATED.code
            val eventData = mapOf("id" to postId)
            val message = OutboxMessageEntity(
                id = messageId,
                type = eventTypeCode,
                data = eventData,
                traceId = traceId,
            )
            val messages = listOf(message)
            val event = PostEvent.Created(id = postId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns messages
            every { objectMapper.convertValue(any(), any<Class<PostEvent>>()) } returns event
            every { postEventPublisher.publish(any(), any(), any()) } just runs

            // When
            postEventProcessor.processPostCreatedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, PostEvent.Created::class.java) }
            verify(exactly = 1) { postEventPublisher.publish(event, message.traceId, message.id!!) }
        }
    }

    @Nested
    @DisplayName("PostEventProcessor :: processPostDeletedEvents")
    inner class ProcessPostDeletedEventsTest {
        @Test
        fun `should publish PostEvent#Deleted using PostEventPublisher instance`() {
            // Given
            val eventTypeCode = PostEvent.Type.POST_EVENT_DELETED.code
            val eventData = mapOf("ids" to setOf(postId))
            val message = OutboxMessageEntity(
                id = messageId,
                type = eventTypeCode,
                data = eventData,
                traceId = traceId,
            )
            val messages = listOf(message)
            val event = PostEvent.Deleted(id = postId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns messages
            every { objectMapper.convertValue(any(), any<Class<PostEvent>>()) } returns event
            every { postEventPublisher.publish(any(), any(), any()) } just runs

            // When
            postEventProcessor.processPostDeletedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, PostEvent.Deleted::class.java) }
            verify(exactly = 1) { postEventPublisher.publish(event, message.traceId, message.id!!) }
        }
    }

    companion object {
        private val postId = UUID.randomUUID()
        private val traceId = UUID.randomUUID()
        private val messageId = UUID.randomUUID()
    }
}
