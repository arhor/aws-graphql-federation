package com.github.arhor.aws.graphql.federation.votes.config

import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "currentDateTimeProvider")
@EnableJdbcRepositories(basePackages = ["com.github.arhor.aws.graphql.federation.votes.data.repository"])
@EnableTransactionManagement
class ConfigureDatabase : AbstractJdbcConfiguration() {

    @Bean
    fun flywayConfigurationCustomizer() = FlywayConfigurationCustomizer {
        it.loggers("slf4j")
    }
}
