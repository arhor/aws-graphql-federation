package com.github.arhor.aws.graphql.federation.tracing

import org.slf4j.event.Level
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tracing")
data class TracingProperties(
    val methodExecutionLogging: MethodExecutionLogging = MethodExecutionLogging(),
) {
    data class MethodExecutionLogging(
        val enabled: Boolean = false,
        val level: Level = Level.DEBUG,
    )
}
