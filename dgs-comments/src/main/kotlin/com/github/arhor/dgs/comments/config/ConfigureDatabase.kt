package com.github.arhor.dgs.comments.config

import com.github.arhor.dgs.lib.config.AbstractDatabaseConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration(proxyBeanMethods = false)
@EnableJdbcRepositories(basePackages = ["com.github.arhor.dgs.comments.data.repository"])
class ConfigureDatabase : AbstractDatabaseConfiguration()
