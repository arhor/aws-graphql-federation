package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.starter.security.PredefinedAuthority
import com.github.arhor.aws.graphql.federation.users.data.entity.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.entity.AuthRef
import com.github.arhor.aws.graphql.federation.users.data.entity.callback.UserEntityCallback
import com.github.arhor.aws.graphql.federation.users.data.repository.mapping.UserIdToAuthNamesResultSetExtractor
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        UserEntityCallback::class,
        UserIdToAuthNamesResultSetExtractor::class,
    ]
)
class AuthRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var authRepository: AuthRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Nested
    @DisplayName("AuthRepository :: findAllByUserIdIn")
    inner class FindAllByUserIdInTest {
        @Test
        fun `should return map with non-empty list of authority names fetching authorities for a given user`() {
            // Given
            val authorities = authRepository.saveAll(
                listOf(
                    AuthEntity(name = "test-create"),
                    AuthEntity(name = "test-update"),
                    AuthEntity(name = "test-delete"),
                )
            )
            val createdUser = userRepository.createAndSaveTestUser(
                authorities = authorities
                    .map { AuthRef.from(it) }
                    .toSet()
            )
            val expectedAuthNames = authorities.map { it.name }

            // When
            val result = authRepository.findAllByUserIdIn(listOf(createdUser.id!!))

            // Then
            assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasEntrySatisfying(createdUser.id) {
                    assertThat(it)
                        .isNotNull()
                        .isNotEmpty()
                        .containsExactlyInAnyOrderElementsOf(expectedAuthNames)
                }
        }

        @Test
        fun `should return map with empty list of authority names fetching authorities for a given user`() {
            // Given
            val user = userRepository.createAndSaveTestUser()

            // When
            val result = authRepository.findAllByUserIdIn(listOf(user.id!!))

            // Then
            assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasEntrySatisfying(user.id) {
                    assertThat(it)
                        .isNotNull()
                        .isEmpty()
                }
        }
    }

    @Nested
    @DisplayName("AuthRepository :: findByName")
    inner class FindByNameTest {
        @EnumSource
        @ParameterizedTest
        fun `should return AuthEntity with expected name for each PredefinedAuthorities entry`(
            // Given
            authority: PredefinedAuthority,
        ) {
            // When
            val result = authRepository.findByName(authority)

            // Then
            assertThat(result)
                .isNotNull()
                .returns(authority.name, from { it!!.name })
        }
    }
}
