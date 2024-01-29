package com.github.arhor.aws.graphql.federation.users.service.events

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxEventRepository
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
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
internal class OutboxEventProcessorTest {

    @EnableRetry
    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [OutboxEventProcessor::class])],
        useDefaultFilters = false,
    )
    class Config

    @MockkBean
    private lateinit var outboxEventRepository: OutboxEventRepository

    @MockkBean
    private lateinit var outboxEventPublisher: OutboxEventPublisher

    @Autowired
    private lateinit var outboxEventProcessor: OutboxEventProcessor

    @Test
    fun `should publish outbox events using OutboxEventPublisher instance`() {
        val outboxEvent = OutboxEventEntity(
            type = UserEvent.USER_EVENT_DELETED,
            payload = mapOf("id" to 1),
            headers = mapOf("type" to UserEvent.USER_EVENT_DELETED),
        )

        // given
        every { outboxEventRepository.dequeueOldest(any(), any()) } returns listOf(outboxEvent)
        every { outboxEventPublisher.publish(any()) } just runs
        every { outboxEventRepository.delete(any()) } just runs

        // when
        outboxEventProcessor.processOutboxEvents()

        // then
        verify(exactly = 1) { outboxEventRepository.dequeueOldest(UserEvent.USER_EVENT_DELETED, 10) }
        verify(exactly = 1) { outboxEventPublisher.publish(outboxEvent) }
        verify(exactly = 1) { outboxEventRepository.delete(outboxEvent) }
    }
}
