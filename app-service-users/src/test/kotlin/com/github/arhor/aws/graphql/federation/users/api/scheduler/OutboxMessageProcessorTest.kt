package com.github.arhor.aws.graphql.federation.users.api.scheduler

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

class OutboxMessageProcessorTest {

    private val outboxMessageService = mockk<OutboxMessageService>()
    private val outboxMessageProcessor = OutboxMessageProcessor(outboxMessageService)

    @AfterEach
    fun tearDown() {
        confirmVerified(outboxMessageService)
    }

    @Nested
    @DisplayName("Method processOutboxMessages")
    inner class ProcessUserCreatedEventsTest {
        @Test
        fun `should publish UserEvent#Created using UserEventPublisher instance`() {
            // Given
            every { outboxMessageService.releaseOutboxMessages(any()) } just runs

            // When
            outboxMessageProcessor.processOutboxMessages()

            // Then
            verify(exactly = 1) { outboxMessageService.releaseOutboxMessages(limit = 50) }
        }
    }
}
