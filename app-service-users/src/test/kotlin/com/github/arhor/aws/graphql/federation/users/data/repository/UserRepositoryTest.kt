package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.model.callback.UserEntityCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [UserEntityCallback::class])
class UserRepositoryTest : RepositoryTestBase() {

    @Test
    fun `should return true for the username of an existing user`() {
        // Given
        val createdUser = createAndSaveTestUser()

        // When
        val result = userRepository.existsByUsername(createdUser.username)

        // Then
        assertThat(result)
            .isTrue()
    }

    @Test
    fun `should return false for the email of a non-existing user`() {
        // Given
        val createdUser = createAndSaveTestUser()

        // When
        userRepository.delete(createdUser)
        val result = userRepository.existsByUsername(createdUser.username)

        // Then
        assertThat(result)
            .isFalse()
    }
}
