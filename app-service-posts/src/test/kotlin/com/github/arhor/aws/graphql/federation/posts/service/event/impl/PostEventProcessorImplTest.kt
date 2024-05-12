package com.github.arhor.aws.graphql.federation.posts.service.event.impl

import com.fasterxml.jackson.core.type.TypeReference
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
        fun `should publish PostEvent#Created using OutboxEventPublisher instance`() {
            // Given
            val postId = UUID.randomUUID()
            val eventTypeCode = PostEvent.Type.POST_EVENT_CREATED.code
            val eventData = mapOf("id" to postId)
            val outboxMessage = OutboxMessageEntity(
                id = UUID.randomUUID(),
                type = eventTypeCode,
                data = eventData,
                traceId = UUID.randomUUID(),
            )
            val outboxEvents = listOf(outboxMessage)
            val event = PostEvent.Created(id = postId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns outboxEvents
            every { objectMapper.convertValue(any(), any<TypeReference<PostEvent.Created>>()) } returns event
            every { postEventPublisher.publish(any(), any()) } just runs

            // When
            postEventProcessor.processPostCreatedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, any<TypeReference<PostEvent.Created>>()) }
            verify(exactly = 1) { postEventPublisher.publish(event, outboxMessage.traceId) }
        }
    }

    @Nested
    @DisplayName("PostEventProcessor :: processPostDeletedEvents")
    inner class ProcessPostDeletedEventsTest {
        @Test
        fun `should publish PostEvent#Deleted using OutboxEventPublisher instance`() {
            // Given
            val postId = UUID.randomUUID()
            val eventTypeCode = PostEvent.Type.POST_EVENT_DELETED.code
            val eventData = mapOf("ids" to setOf(postId))
            val outboxMessage = OutboxMessageEntity(
                id = UUID.randomUUID(),
                type = eventTypeCode,
                data = eventData,
                traceId = UUID.randomUUID(),
            )
            val outboxEvents = listOf(outboxMessage)
            val event = PostEvent.Deleted(id = postId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns outboxEvents
            every { objectMapper.convertValue(any(), any<TypeReference<PostEvent.Deleted>>()) } returns event
            every { postEventPublisher.publish(any(), any()) } just runs

            // When
            postEventProcessor.processPostDeletedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, any<TypeReference<PostEvent.Deleted>>()) }
            verify(exactly = 1) { postEventPublisher.publish(event, outboxMessage.traceId) }
        }
    }
}
