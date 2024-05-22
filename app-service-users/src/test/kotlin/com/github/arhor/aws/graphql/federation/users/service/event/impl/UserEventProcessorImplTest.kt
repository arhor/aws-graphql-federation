package com.github.arhor.aws.graphql.federation.users.service.event.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.event.UserEventPublisher
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

class UserEventProcessorImplTest {

    private val objectMapper = mockk<ObjectMapper>()
    private val outboxMessageRepository = mockk<OutboxMessageRepository>()
    private val userEventPublisher = mockk<UserEventPublisher>()

    private val userEventProcessor = UserEventProcessorImpl(
        objectMapper,
        outboxMessageRepository,
        userEventPublisher,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(
            objectMapper,
            outboxMessageRepository,
            userEventPublisher,
        )
    }

    @Nested
    @DisplayName("UserEventProcessor :: processUserCreatedEvents")
    inner class ProcessUserCreatedEventsTest {
        @Test
        fun `should publish UserEvent#Created using UserEventPublisher instance`() {
            // Given
            val eventTypeCode = UserEvent.Type.USER_EVENT_CREATED.code
            val eventData = mapOf("id" to userId)
            val message = OutboxMessageEntity(
                id = messageId,
                type = eventTypeCode,
                data = eventData,
                traceId = traceId,
            )
            val messages = listOf(message)
            val event = UserEvent.Created(id = userId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns messages
            every { objectMapper.convertValue(any(), any<Class<UserEvent>>()) } returns event
            every { userEventPublisher.publish(any(), any(), any()) } just runs

            // When
            userEventProcessor.processUserCreatedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, UserEvent.Created::class.java) }
            verify(exactly = 1) { userEventPublisher.publish(event, message.traceId, message.id!!) }
        }
    }

    @Nested
    @DisplayName("UserEventProcessor :: processUserDeletedEvents")
    inner class ProcessUserDeletedEventsTest {
        @Test
        fun `should publish UserEvent#Deleted using UserEventPublisher instance`() {
            // Given
            val eventTypeCode = UserEvent.Type.USER_EVENT_DELETED.code
            val eventData = mapOf("id" to userId)
            val message = OutboxMessageEntity(
                id = messageId,
                type = eventTypeCode,
                data = eventData,
                traceId = traceId,
            )
            val messages = listOf(message)
            val event = UserEvent.Deleted(id = userId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns messages
            every { objectMapper.convertValue(any(), any<Class<UserEvent>>()) } returns event
            every { userEventPublisher.publish(any(), any(), any()) } just runs

            // When
            userEventProcessor.processUserDeletedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, UserEvent.Deleted::class.java) }
            verify(exactly = 1) { userEventPublisher.publish(event, message.traceId, message.id!!) }
        }
    }

    companion object {
        private val userId = UUID.randomUUID()
        private val traceId = UUID.randomUUID()
        private val messageId = UUID.randomUUID()
    }
}
