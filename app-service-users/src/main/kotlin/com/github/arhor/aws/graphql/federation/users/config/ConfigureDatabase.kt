package com.github.arhor.aws.graphql.federation.users.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.users.data.converter.JsonWritingConverter
import com.github.arhor.aws.graphql.federation.users.data.converter.JsonReadingConverter
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
@EnableJdbcRepositories(basePackages = ["com.github.arhor.aws.graphql.federation.users.data.repository"])
@EnableTransactionManagement
class ConfigureDatabase(private val objectMapper: ObjectMapper) : AbstractJdbcConfiguration() {

    override fun userConverters() = listOf(
        JsonReadingConverter(objectMapper),
        JsonWritingConverter(objectMapper),
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
