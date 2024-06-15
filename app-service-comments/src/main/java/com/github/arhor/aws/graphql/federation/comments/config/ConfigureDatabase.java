package com.github.arhor.aws.graphql.federation.comments.config;

import com.github.arhor.aws.graphql.federation.comments.data.entity.Commentable;
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
        return List.of(
            new FeaturesReadingConverter<>(Commentable.Feature.class),
            new FeaturesWritingConverter<>(Commentable.Feature.class)
        );
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return (config) -> config.loggers("slf4j");
    }
}
