package com.github.arhor.dgs.users.config

import com.github.arhor.dgs.lib.config.AbstractDatabaseConfiguration
import com.github.arhor.dgs.users.data.entity.converter.SettingsReadingConverter
import com.github.arhor.dgs.users.data.entity.converter.SettingsWritingConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration(proxyBeanMethods = false)
@EnableJdbcRepositories(basePackages = ["com.github.arhor.dgs.users.data.repository"])
class ConfigureDatabase : AbstractDatabaseConfiguration() {

    override fun userConverters() = listOf(
        SettingsReadingConverter,
        SettingsWritingConverter,
    )
}
