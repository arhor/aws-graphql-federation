package com.github.arhor.aws.graphql.federation.comments.config;

import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments;
import com.github.arhor.aws.graphql.federation.spring.core.data.FeaturesReadingConverter;
import com.github.arhor.aws.graphql.federation.spring.core.data.FeaturesWritingConverter;
import jakarta.annotation.Nonnull;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "currentDateTimeProvider")
@EnableJdbcRepositories(basePackages = {"com.github.arhor.aws.graphql.federation.comments.data.repository"})
@EnableTransactionManagement
public class ConfigureDatabase extends AbstractJdbcConfiguration {

    @Nonnull
    @Override
    public List<?> userConverters() {
        return List.of(
            new FeaturesReadingConverter<>(HasComments.Feature.class),
            new FeaturesWritingConverter<>(HasComments.Feature.class)
        );
    }

    @Bean
    public DateTimeProvider currentDateTimeProvider(final Supplier<LocalDateTime> currentDateTimeSupplier) {
        return () -> Optional.of(currentDateTimeSupplier.get());
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return (config) -> config.loggers("slf4j");
    }
}
