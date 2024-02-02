package com.github.arhor.aws.graphql.federation.users.data.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UserRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should return true for the email of an existing user`() {
        // given
        val createdUser = userRepository.createAndSaveTestUser()

        // When
        val result = userRepository.existsByUsername(createdUser.username)

        // then
        assertThat(result)
            .isTrue()
    }

    @Test
    fun `should return false for the email of a non-existing user`() {
        // given
        val createdUser = userRepository.createAndSaveTestUser()

        // When
        userRepository.delete(createdUser)
        val result = userRepository.existsByUsername(createdUser.username)

        // then
        assertThat(result)
            .isFalse()
    }
}
