package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.async.ConfigureAdditionalBeans
import com.github.arhor.aws.graphql.federation.users.config.ConfigureDatabase
import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import com.github.arhor.aws.graphql.federation.users.test.TestObjectMapperConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = [
        ConfigureDatabase::class,
        ConfigureAdditionalBeans::class,
        TestObjectMapperConfig::class,
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class UserEntityRepositoryIntegrationTest(
    @Autowired
    private val userRepository: UserRepository,
) {

    @Test
    fun `should return true for the email of an existing user`() {
        // Given
        val createdUser = createPersistedUser()

        // When
        val result = userRepository.existsByUsername(createdUser.username)

        // Then
        assertThat(result)
            .isTrue()
    }

    @Test
    fun `should return false for the email of a non-existing user`() {
        // Given
        val deletedUser = createPersistedUser().also { userRepository.delete(it) }

        // When
        val result = userRepository.existsByUsername(deletedUser.username)

        // Then
        assertThat(result)
            .isFalse()
    }

    private fun createPersistedUser(): UserEntity =
        userRepository.save(
            UserEntity(
                username = "test-username",
                password = "test-password",
            )
        )

    companion object {
        @JvmStatic
        @Container
        private val db = PostgreSQLContainer("postgres:12")

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("spring.datasource.url", db::getJdbcUrl)
                add("spring.datasource.username", db::getUsername)
                add("spring.datasource.password", db::getPassword)
            }
        }
    }
}
