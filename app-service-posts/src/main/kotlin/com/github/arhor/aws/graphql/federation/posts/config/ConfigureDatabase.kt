package com.github.arhor.aws.graphql.federation.posts.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation.UserFeature
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation.UserFeatures
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesReadingConverter
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesWritingConverter
import com.github.arhor.aws.graphql.federation.starter.core.data.JsonReadingConverter
import com.github.arhor.aws.graphql.federation.starter.core.data.JsonWritingConverter
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "currentDateTimeProvider")
@EnableJdbcRepositories(basePackages = ["com.github.arhor.aws.graphql.federation.posts.data.repository"])
@EnableTransactionManagement
class ConfigureDatabase(private val objectMapper: ObjectMapper) : AbstractJdbcConfiguration() {

    override fun userConverters(): List<Converter<*, *>> {
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
        return listOf(
            JsonReadingConverter(objectMapper),
            JsonWritingConverter(objectMapper),
            object : FeaturesReadingConverter<UserFeatures, UserFeature>(UserFeature::class.java, ::UserFeatures) {},
            FeaturesWritingConverter(),
        )
    }

    @Bean
    fun flywayConfigurationCustomizer() = FlywayConfigurationCustomizer {
        it.loggers("slf4j")
    }
}
