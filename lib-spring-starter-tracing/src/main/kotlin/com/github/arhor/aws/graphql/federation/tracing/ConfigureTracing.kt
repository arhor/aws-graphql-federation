package com.github.arhor.aws.graphql.federation.tracing

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration
@ComponentScan
@ConfigurationPropertiesScan
@ConditionalOnProperty(prefix = "tracing", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class ConfigureTracing
