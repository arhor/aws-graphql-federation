package com.github.arhor.dgs.topics.config

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
@EnableJdbcRepositories(basePackages = ["com.github.arhor.dgs.topics.data.repository"])
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "currentDateTimeProvider")
@EnableTransactionManagement
class ConfigureDatabase : AbstractJdbcConfiguration() {

    @Bean
    fun currentDateTimeProvider(currentDateTimeSupplier: Supplier<LocalDateTime>) = DateTimeProvider {
        Optional.of(currentDateTimeSupplier.get())
    }

    @Bean
    fun flywayConfigurationCustomizer() = FlywayConfigurationCustomizer {
        it.loggers("slf4j")
    }
}
