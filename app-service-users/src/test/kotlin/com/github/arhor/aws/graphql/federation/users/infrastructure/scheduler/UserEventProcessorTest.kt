package com.github.arhor.aws.graphql.federation.users.infrastructure.scheduler

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
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

class UserEventProcessorTest {

    private val outboxMessageService = mockk<OutboxMessageService>()
    private val userEventProcessor = UserEventProcessor(outboxMessageService)

    @AfterEach
    fun tearDown() {
        confirmVerified(outboxMessageService)
    }

    @Nested
    @DisplayName("Method processUserCreatedEvents")
    inner class ProcessUserCreatedEventsTest {
        @Test
        fun `should publish UserEvent#Created using UserEventPublisher instance`() {
            // Given
            every { outboxMessageService.releaseOutboxMessagesOfType(any()) } just runs

            // When
            userEventProcessor.processUserCreatedEvents()

            // Then
            verify(exactly = 1) { outboxMessageService.releaseOutboxMessagesOfType(UserEvent.Type.USER_EVENT_CREATED) }
        }
    }

    @Nested
    @DisplayName("Method processUserDeletedEvents")
    inner class ProcessUserDeletedEventsTest {
        @Test
        fun `should publish UserEvent#Deleted using UserEventPublisher instance`() {
            // Given
            every { outboxMessageService.releaseOutboxMessagesOfType(any()) } just runs

            // When
            userEventProcessor.processUserDeletedEvents()

            // Then
            verify(exactly = 1) { outboxMessageService.releaseOutboxMessagesOfType(UserEvent.Type.USER_EVENT_DELETED) }
        }
    }
}
