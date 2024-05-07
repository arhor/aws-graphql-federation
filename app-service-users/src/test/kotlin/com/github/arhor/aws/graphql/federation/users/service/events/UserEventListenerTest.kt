package com.github.arhor.aws.graphql.federation.users.service.events

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

private typealias OutboxEventData = TypeReference<Map<String, Any?>>

@SpringJUnitConfig
internal class UserEventListenerTest {

    @EnableRetry
    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [UserEventListener::class])],
        useDefaultFilters = false,
    )
    class Config

    @MockkBean
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var outboxMessageRepository: OutboxMessageRepository

    @Autowired
    private lateinit var userEventEmitter: UserEventListener

    @Test
    fun `should save UserEvent to the OutboxEventRepository`() {
        // Given
        val event = UserEvent.Deleted(ids = setOf(1L))

        every { objectMapper.convertValue(any(), any<OutboxEventData>()) } returns mockk()
        every { outboxMessageRepository.save(any()) } returns mockk()

        // When
        userEventEmitter.onUserEvent(event)

        // Then
        verify(exactly = 1) { objectMapper.convertValue(any(), any<OutboxEventData>()) }
        verify(exactly = 1) { outboxMessageRepository.save(any()) }
    }
}
