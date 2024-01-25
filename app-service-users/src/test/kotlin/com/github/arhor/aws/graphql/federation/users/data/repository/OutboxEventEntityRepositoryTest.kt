package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import com.github.arhor.aws.graphql.federation.users.data.entity.callback.OutboxEventEntityCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [OutboxEventEntityCallback::class])
internal class OutboxEventEntityRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var outboxEventRepository: OutboxEventRepository

    @Test
    fun `should deque existing outbox events removing them from the DB`() {
        // given
        val expectedSizeOfBatch = 5
        val expectedEventsAtAll = expectedSizeOfBatch * 2

        val outboxEvents = outboxEventRepository.saveAll(
            (1..expectedEventsAtAll).map {
                OutboxEventEntity(
                    type = "test-event-$it",
                    payload = emptyMap(),
                    headers = emptyMap(),
                )
            }
        )

        // when
        val allOutboxEventsBefore = outboxEventRepository.findAll()
        val outboxEvents1 = outboxEventRepository.dequeueOldest(expectedSizeOfBatch)
        val outboxEvents2 = outboxEventRepository.dequeueOldest(expectedSizeOfBatch)
        val allOutboxEventsAfter = outboxEventRepository.findAll()

        // then
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
