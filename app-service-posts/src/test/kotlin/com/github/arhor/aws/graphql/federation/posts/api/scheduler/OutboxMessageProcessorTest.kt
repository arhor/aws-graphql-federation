package com.github.arhor.aws.graphql.federation.posts.api.scheduler

import com.github.arhor.aws.graphql.federation.posts.service.OutboxMessageService
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
    fun `confirm that all interactions with mocked dependencies were verified`() {
        confirmVerified(outboxMessageService)
    }

    @Nested
    @DisplayName("Method processOutboxMessages")
    inner class ProcessPostCreatedEventsTest {
        @Test
        fun `should publish PostEvent#Created using PostEventPublisher instance`() {
            // Given
            every { outboxMessageService.releaseOutboxMessages(any()) } just runs

            // When
            outboxMessageProcessor.processOutboxMessages()

            // Then
            verify(exactly = 1) { outboxMessageService.releaseOutboxMessages(limit = 50) }
        }
    }
}
