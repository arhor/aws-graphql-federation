package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.config.ConfigureDatabase;
import com.github.arhor.aws.graphql.federation.starter.core.CoreComponentsAutoConfiguration;
import com.github.arhor.aws.graphql.federation.starter.testing.ConfigureTestObjectMapper;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = {
        CoreComponentsAutoConfiguration.class,
        ConfigureDatabase.class,
        ConfigureTestObjectMapper.class,
    }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class RepositoryTestBase {

    @Container
    private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:13-alpine");

    @DynamicPropertySource
    public static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt.getZERO_UUID_VAL();

        registry.add("spring.datasource.url", DB::getJdbcUrl);
        registry.add("spring.datasource.username", DB::getUsername);
        registry.add("spring.datasource.password", DB::getPassword);
    }
}
