package com.github.arhor.dgs.extradata.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.dgs.extradata.data.entity.converter.JsonReadingConverter
import com.github.arhor.dgs.extradata.data.entity.converter.JsonWritingConverter
import com.github.arhor.dgs.lib.config.AbstractDatabaseConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration(proxyBeanMethods = false)
@EnableJdbcRepositories(basePackages = ["com.github.arhor.dgs.extradata.data.repository"])
class ConfigureDatabase(private val objectMapper: ObjectMapper) : AbstractDatabaseConfiguration() {

    override fun userConverters() = listOf(
        JsonReadingConverter(objectMapper),
        JsonWritingConverter(objectMapper),
    )
}
