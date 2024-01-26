package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.entity.AuthRef
import com.github.arhor.aws.graphql.federation.users.data.repository.mapping.UserIdToAuthNamesResultSetExtractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [UserIdToAuthNamesResultSetExtractor::class])
internal class AuthRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var authRepository: AuthRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should return an empty list fetching authorities for a new user`() {
        // given
        val authorities = authRepository.saveAll(
            listOf(
                AuthEntity(name = "test-create"),
                AuthEntity(name = "test-update"),
                AuthEntity(name = "test-delete"),
            )
        )
        val createdUser = userRepository.createAndSaveTestUser(
            authorities = authorities
                .map { AuthRef.create(it) }
                .toSet()
        )

        // when
        val result = authRepository.findAllByUserIdIn(listOf(createdUser.id!!))

        // then
        assertThat(result)
            .isNotNull()
            .isNotEmpty()
            .hasEntrySatisfying(createdUser.id) {
                assertThat(it)
                    .isNotNull()
                    .isNotEmpty()
                    .containsExactlyInAnyOrderElementsOf(authorities.map(AuthEntity::name))
            }
    }
}
