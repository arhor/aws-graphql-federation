package com.github.arhor.aws.graphql.federation.comments.config;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesReadingConverter;
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesWritingConverter;
import jakarta.annotation.Nonnull;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "currentDateTimeProvider")
@EnableJdbcRepositories(basePackages = {"com.github.arhor.aws.graphql.federation.comments.data.repository"})
@EnableTransactionManagement
public class ConfigureDatabase extends AbstractJdbcConfiguration {

    @Nonnull
    @Override
    public List<?> userConverters() {
        final var userFeatures = UserRepresentation.Feature.class;
        final var postFeatures = PostRepresentation.Feature.class;

        return List.of(
            new FeaturesReadingConverter<>(userFeatures),
            new FeaturesWritingConverter<>(userFeatures),
            new FeaturesReadingConverter<>(postFeatures),
            new FeaturesWritingConverter<>(postFeatures)
        );
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return (config) -> config.loggers("slf4j");
    }
}
