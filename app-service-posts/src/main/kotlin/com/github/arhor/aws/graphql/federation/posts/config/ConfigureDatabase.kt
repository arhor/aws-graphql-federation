package com.github.arhor.aws.graphql.federation.posts.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation
import com.github.arhor.aws.graphql.federation.spring.core.data.FeaturesReadingConverter
import com.github.arhor.aws.graphql.federation.spring.core.data.FeaturesWritingConverter
import com.github.arhor.aws.graphql.federation.spring.core.data.JsonReadingConverter
import com.github.arhor.aws.graphql.federation.spring.core.data.JsonWritingConverter
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.time.LocalDateTime
import java.util.Optional
import java.util.function.Supplier

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "currentDateTimeProvider")
@EnableJdbcRepositories(basePackages = ["com.github.arhor.aws.graphql.federation.posts.data.repository"])
@EnableTransactionManagement
class ConfigureDatabase(private val objectMapper: ObjectMapper) : AbstractJdbcConfiguration() {

    override fun userConverters() = listOf(
        JsonReadingConverter(objectMapper),
        JsonWritingConverter(objectMapper),
        FeaturesReadingConverter(UserRepresentation.Feature::class.java),
        FeaturesWritingConverter(UserRepresentation.Feature::class.java),
    )

    @Bean
    fun currentDateTimeProvider(currentDateTimeSupplier: Supplier<LocalDateTime>) = DateTimeProvider {
        Optional.of(currentDateTimeSupplier.get())
    }

    @Bean
    fun flywayConfigurationCustomizer() = FlywayConfigurationCustomizer {
        it.loggers("slf4j")
    }
}
