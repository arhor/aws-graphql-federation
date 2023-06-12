package com.github.arhor.dgs.topics.config

import com.github.arhor.dgs.lib.config.AbstractDatabaseConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration(proxyBeanMethods = false)
@EnableJdbcRepositories(basePackages = ["com.github.arhor.dgs.topics.data.repository"])
class ConfigureDatabase : AbstractDatabaseConfiguration()
