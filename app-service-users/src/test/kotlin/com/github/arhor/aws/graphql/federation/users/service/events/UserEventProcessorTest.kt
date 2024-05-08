package com.github.arhor.aws.graphql.federation.users.service.events

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.test.ConfigureTestObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.util.UUID

@SpringJUnitConfig
internal class UserEventProcessorTest {

    @EnableRetry
    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [UserEventProcessor::class])],
        useDefaultFilters = false,
    )
    @Import(ConfigureTestObjectMapper::class)
    class Config

    @MockkBean
    private lateinit var outboxMessageRepository: OutboxMessageRepository

    @MockkBean
    private lateinit var outboxEventPublisher: UserEventPublisher

    @Autowired
    private lateinit var userEventProcessor: UserEventProcessor

    @Nested
    @DisplayName("UserEventProcessor :: processUserCreatedEvents")
    inner class ProcessUserCreatedEventsTest {
        @Test
        fun `should publish UserEvent#Created using OutboxEventPublisher instance`() {
            // Given
            val userId = UUID.randomUUID()
            val eventTypeCode = UserEvent.Type.USER_EVENT_CREATED.code
            val outboxEvents = listOf(
                OutboxMessageEntity(
                    type = eventTypeCode,
                    data = mapOf("ids" to setOf(userId)),
                )
            )
            val userEvent = UserEvent.Created(id = userId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns outboxEvents
            every { outboxEventPublisher.publish(any()) } just runs
            every { outboxMessageRepository.deleteAll(any()) } just runs

            // When
            userEventProcessor.processUserCreatedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { outboxEventPublisher.publish(userEvent) }
            verify(exactly = 1) { outboxMessageRepository.deleteAll(outboxEvents) }
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
            val outboxEvents = listOf(
                OutboxMessageEntity(
                    type = eventTypeCode,
                    data = mapOf("ids" to setOf(userId)),
                )
            )
            val userEvent = UserEvent.Deleted(id = userId)

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns outboxEvents
            every { outboxEventPublisher.publish(any()) } just runs
            every { outboxMessageRepository.deleteAll(any()) } just runs

            // When
            userEventProcessor.processUserDeletedEvents()

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventTypeCode, 50) }
            verify(exactly = 1) { outboxEventPublisher.publish(userEvent) }
            verify(exactly = 1) { outboxMessageRepository.deleteAll(outboxEvents) }
        }
    }
}
