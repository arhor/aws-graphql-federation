package com.github.arhor.aws.graphql.federation.users.service.event.impl

import com.fasterxml.jackson.core.type.TypeReference
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
        fun `should publish UserEvent#Created using OutboxEventPublisher instance`() {
            // Given
            val userId = UUID.randomUUID()
            val eventTypeCode = UserEvent.Type.USER_EVENT_CREATED.code
            val eventData = mapOf("id" to userId)
            val outboxMessage = OutboxMessageEntity(
                id = UUID.randomUUID(),
                type = eventTypeCode,
                data = eventData,
            )
            val outboxMessages = listOf(outboxMessage)
            val event = UserEvent.Created(id = userId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns outboxMessages
            every { objectMapper.convertValue(any(), any<TypeReference<UserEvent.Created>>()) } returns event
            every { userEventPublisher.publish(any(), any()) } just runs
            every { outboxMessageRepository.deleteAll(any()) } just runs

            // When
            userEventProcessor.processUserCreatedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, any<TypeReference<UserEvent.Created>>()) }
            verify(exactly = 1) { userEventPublisher.publish(event, outboxMessage.id!!) }
            verify(exactly = 1) { outboxMessageRepository.deleteAll(outboxMessages) }
        }
    }

    @Nested
    @DisplayName("UserEventProcessor :: processUserDeletedEvents")
    inner class ProcessUserDeletedEventsTest {
        @Test
        fun `should publish UserEvent#Deleted using OutboxEventPublisher instance`() {
            // Given
            val userId = UUID.randomUUID()
            val eventTypeCode = UserEvent.Type.USER_EVENT_DELETED.code
            val eventData = mapOf("id" to userId)
            val outboxMessage = OutboxMessageEntity(
                id = UUID.randomUUID(),
                type = eventTypeCode,
                data = eventData,
            )
            val outboxMessages = listOf(outboxMessage)
            val event = UserEvent.Deleted(id = userId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns outboxMessages
            every { objectMapper.convertValue(any(), any<TypeReference<UserEvent.Deleted>>()) } returns event
            every { userEventPublisher.publish(any(), any()) } just runs
            every { outboxMessageRepository.deleteAll(any()) } just runs

            // When
            userEventProcessor.processUserDeletedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, any<TypeReference<UserEvent.Deleted>>()) }
            verify(exactly = 1) { userEventPublisher.publish(event, outboxMessage.id!!) }
            verify(exactly = 1) { outboxMessageRepository.deleteAll(outboxMessages) }
        }
    }
}
