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
        outboxEventRepository.saveAll(
            sequence {
                repeat(10) {
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
        val outboxEvents1 = outboxEventRepository.dequeueOldest(5)
        val outboxEvents2 = outboxEventRepository.dequeueOldest(5)

        // then
        assertThat(outboxEvents1)
            .isNotNull()
            .hasSize(5)

        assertThat(outboxEvents2)
            .isNotNull()
            .hasSize(5)

        assertThat(outboxEvents1)
            .doesNotContainAnyElementsOf(outboxEvents2)
    }
}
