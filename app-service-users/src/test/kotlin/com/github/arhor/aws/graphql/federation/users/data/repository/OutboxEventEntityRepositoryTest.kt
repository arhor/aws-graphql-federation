package com.github.arhor.aws.graphql.federation.users.data.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class OutboxEventEntityRepositoryTest(
    @Autowired
    private val outboxEventRepository: OutboxEventRepository,
) : RepositoryTestBase() {

    @Test
    fun `should return true for the email of an existing user`() {
        // given

        // when

        // then

    }
}
