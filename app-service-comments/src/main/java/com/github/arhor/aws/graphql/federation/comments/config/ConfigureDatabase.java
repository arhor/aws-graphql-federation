package com.github.arhor.aws.graphql.federation.comments.config;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation.PostFeature;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation.PostFeatures;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation.UserFeature;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation.UserFeatures;
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesReadingConverter;
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesWritingConverter;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    @Override
    public List<?> userConverters() {
        // The reason to use anonymous classes.
        //
        // Generics in Java are erased during compilation, the only exception I know - generic type arguments
        // used during inheritance. So, it's impossible to reify generic type argument from the following
        // declaration:
        // final var list = new ArrayList<Integer>()
        //
        // But, it's possible if we use inheritance:
        // class IntegerArrayList extends ArrayList<Integer> {}
        // final var list = new IntegerArrayList()
        //
        // The same approach works with anonymous classes, since they syntax mixes object instantiation with
        // class declaration.
        return List.of(
            new FeaturesReadingConverter<>(UserFeature.class, UserFeatures::new) {},
            new FeaturesReadingConverter<>(PostFeature.class, PostFeatures::new) {},
            new FeaturesWritingConverter()
        );
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return (config) -> config.loggers("slf4j");
    }
}
