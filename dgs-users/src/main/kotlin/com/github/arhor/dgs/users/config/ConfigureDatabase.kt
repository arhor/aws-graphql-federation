package com.github.arhor.dgs.users.config

import com.github.arhor.dgs.users.data.converter.EnumSetReadingConverter
import com.github.arhor.dgs.users.data.converter.EnumSetWritingConverter
import com.github.arhor.dgs.users.generated.graphql.types.Setting
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
@EnableJdbcRepositories(basePackages = ["com.github.arhor.dgs.users.data.repository"])
@EnableTransactionManagement
class ConfigureDatabase : AbstractJdbcConfiguration() {

    override fun userConverters() = listOf(
        EnumSetReadingConverter(Setting::class.java),
        EnumSetWritingConverter(Setting::class.java),
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
