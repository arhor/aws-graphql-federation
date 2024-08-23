package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.starter.core.CoreComponentsAutoConfiguration
import com.github.arhor.aws.graphql.federation.starter.testing.ConfigureTestObjectMapper
import com.github.arhor.aws.graphql.federation.users.config.ConfigureDatabase
import com.github.arhor.aws.graphql.federation.users.data.model.AuthRef
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import org.junit.jupiter.api.Tag
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

@Tag("integration")
@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = [
        CoreComponentsAutoConfiguration::class,
        ConfigureDatabase::class,
        ConfigureTestObjectMapper::class,
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class RepositoryTestBase {

    @Autowired
    protected lateinit var authRepository: AuthRepository

    @Autowired
    protected lateinit var userRepository: UserRepository

    protected fun createAndSaveTestUser(authorities: Set<AuthRef> = emptySet()): UserEntity =
        userRepository.save(
            UserEntity(
                username = "test-username",
                password = "test-password",
                authorities = authorities,
            )
        )

    companion object {
        @JvmStatic
        @Container
        private val db = PostgreSQLContainer("postgres:13-alpine")

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }
}
