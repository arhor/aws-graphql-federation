package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.config.ConfigureDatabase
import com.github.arhor.aws.graphql.federation.posts.test.ConfigureTestObjectMapper
import com.github.arhor.aws.graphql.federation.spring.core.ConfigureCoreApplicationComponents
import org.junit.jupiter.api.Tag
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
        ConfigureCoreApplicationComponents::class,
        ConfigureDatabase::class,
        ConfigureTestObjectMapper::class,
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal abstract class RepositoryTestBase {

    companion object {
        @JvmStatic
        @Container
        private val db = PostgreSQLContainer("postgres:12-alpine")

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }
}
