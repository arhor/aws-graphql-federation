package com.github.arhor.aws.graphql.federation.comments.config;

import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "currentDateTimeProvider")
@EnableJdbcRepositories(basePackages = {"com.github.arhor.aws.graphql.federation.comments.data.repository"})
@EnableTransactionManagement
public class ConfigureDatabase {

    @Bean
    public DateTimeProvider currentDateTimeProvider(final Supplier<LocalDateTime> currentDateTimeSupplier) {
        return () -> Optional.of(currentDateTimeSupplier.get());
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return (config) -> config.loggers("slf4j");
    }
}
