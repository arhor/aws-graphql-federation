package com.github.arhor.dgs.users.data.repository

import com.github.arhor.dgs.lib.config.ConfigureAdditionalBeans
import com.github.arhor.dgs.users.config.ConfigureDatabase
import com.github.arhor.dgs.users.data.entity.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.jdbc.core.JdbcTemplate
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
@ContextConfiguration(classes = [ConfigureDatabase::class, ConfigureAdditionalBeans::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class UserEntityRepositoryIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `should return true for the email of an existing user`() {
        // Given
        val user = userRepository.save(UserEntity(username = "test-username", password = "test-password"))

        // When
        val result = userRepository.existsByUsername(user.username)

        // Then
        assertThat(result)
            .isTrue()
    }

//    @Test
//    fun `should return false for the email of a non-existing user`() {
//        // Given
//        val notPersistedUser = UserEntity(
//            username = "test2@email.com",
//            password = "TestPassword123",
//        )
//
//        // When
//        val result = userRepository.existsByUsername(notPersistedUser.username)
//
//        // Then
//        assertThat(result)
//            .isFalse()
//    }

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
