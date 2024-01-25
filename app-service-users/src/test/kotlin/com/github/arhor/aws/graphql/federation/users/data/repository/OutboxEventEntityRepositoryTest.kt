package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class OutboxEventEntityRepositoryTest(
    @Autowired
    private val outboxEventRepository: OutboxEventRepository,
) : RepositoryTestBase() {

    @Test
    fun `should return true for the email of an existing user`() {
        // given
        val expectedSizeOfBatch = 5

        outboxEventRepository.saveAll(
            sequence {
                repeat(times = expectedSizeOfBatch * 2) {
                    yield(
                        OutboxEventEntity(
                            type = "test-event-$it",
                            payload = emptyMap(),
                            headers = emptyMap(),
                        )
                    )
                }
            }.toList()
        )

        // when
        val outboxEvents1 = outboxEventRepository.dequeueOldest(expectedSizeOfBatch)
        val outboxEvents2 = outboxEventRepository.dequeueOldest(expectedSizeOfBatch)

        // then
        assertThat(outboxEvents1)
            .isNotNull()
            .hasSize(expectedSizeOfBatch)

        assertThat(outboxEvents2)
            .isNotNull()
            .hasSize(expectedSizeOfBatch)

        assertThat(outboxEvents1)
            .doesNotContainAnyElementsOf(outboxEvents2)
    }
}
