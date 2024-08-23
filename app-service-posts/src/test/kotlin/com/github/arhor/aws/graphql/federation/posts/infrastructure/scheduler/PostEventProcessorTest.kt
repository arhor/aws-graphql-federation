package com.github.arhor.aws.graphql.federation.posts.infrastructure.scheduler

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
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

class PostEventProcessorTest {

    private val outboxMessageService = mockk<OutboxMessageService>()
    private val postEventProcessor = PostEventProcessor(outboxMessageService)

    @AfterEach
    fun tearDown() {
        confirmVerified(outboxMessageService)
    }

    @Nested
    @DisplayName("Method processPostCreatedEvents")
    inner class ProcessPostCreatedEventsTest {
        @Test
        fun `should publish PostEvent#Created using PostEventPublisher instance`() {
            // Given
            every { outboxMessageService.releaseOutboxMessagesOfType(any()) } just runs

            // When
            postEventProcessor.processPostCreatedEvents()

            // Then
            verify(exactly = 1) { outboxMessageService.releaseOutboxMessagesOfType(PostEvent.Type.POST_EVENT_CREATED) }
        }
    }

    @Nested
    @DisplayName("Method processPostDeletedEvents")
    inner class ProcessPostDeletedEventsTest {
        @Test
        fun `should publish PostEvent#Deleted using PostEventPublisher instance`() {
            // Given
            every { outboxMessageService.releaseOutboxMessagesOfType(any()) } just runs

            // When
            postEventProcessor.processPostDeletedEvents()

            // Then
            verify(exactly = 1) { outboxMessageService.releaseOutboxMessagesOfType(PostEvent.Type.POST_EVENT_DELETED) }
        }
    }
}
