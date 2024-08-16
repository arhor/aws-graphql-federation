package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.model.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.model.callback.OutboxMessageEntityCallback
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [OutboxMessageEntityCallback::class])
class OutboxMessageRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var outboxMessageRepository: OutboxMessageRepository

    @Test
    fun `should deque existing outbox events removing them from the DB`() {
        // Given
        val expectedEventType = "test-event"
        val expectedSizeOfBatch = 5
        val expectedEventsAtAll = expectedSizeOfBatch * 2

        val outboxEvents = outboxMessageRepository.saveAll(
            (1..expectedEventsAtAll).map {
                OutboxMessageEntity(
                    type = "test-event",
                    data = emptyMap(),
                    traceId = ZERO_UUID_VAL,
                )
            }
        )

        // When
        val allOutboxEventsBefore = outboxMessageRepository.findAll()
        val outboxEvents1 = outboxMessageRepository.dequeueOldest(expectedEventType, expectedSizeOfBatch)
        val outboxEvents2 = outboxMessageRepository.dequeueOldest(expectedEventType, expectedSizeOfBatch)
        val allOutboxEventsAfter = outboxMessageRepository.findAll()

        // Then
        assertThat(allOutboxEventsBefore)
            .containsExactlyElementsOf(outboxEvents)

        assertThat(outboxEvents1)
            .hasSize(expectedSizeOfBatch)

        assertThat(outboxEvents2)
            .hasSize(expectedSizeOfBatch)

        assertThat(outboxEvents1)
            .doesNotContainAnyElementsOf(outboxEvents2)

        assertThat(allOutboxEventsAfter)
            .isEmpty()
    }
}
