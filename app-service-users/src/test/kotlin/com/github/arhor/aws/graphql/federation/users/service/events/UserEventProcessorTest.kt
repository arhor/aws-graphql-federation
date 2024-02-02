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
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

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

    @Test
    fun `should publish outbox events using OutboxEventPublisher instance`() {
        // Given
        val outboxEvents = listOf(
            OutboxMessageEntity(
                type = UserEvent.USER_EVENT_DELETED,
                data = mapOf("ids" to setOf(1)),
            )
        )
        val userEvent = UserEvent.Deleted(ids = setOf(1))

        every { outboxMessageRepository.dequeueOldest(any(), any()) } returns outboxEvents
        every { outboxEventPublisher.publish(any()) } just runs
        every { outboxMessageRepository.deleteAll(any()) } just runs

        // When
        userEventProcessor.processUserDeletedEvents()

        // Then
        verify(exactly = 1) { outboxMessageRepository.dequeueOldest(UserEvent.USER_EVENT_DELETED, 50) }
        verify(exactly = 1) { outboxEventPublisher.publish(userEvent) }
        verify(exactly = 1) { outboxMessageRepository.deleteAll(outboxEvents) }
    }
}
