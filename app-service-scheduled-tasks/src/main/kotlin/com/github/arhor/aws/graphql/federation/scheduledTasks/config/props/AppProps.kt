package com.github.arhor.aws.graphql.federation.scheduledTasks.config.props

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("app-props")
data class AppProps(
    @field:Valid
    @field:NotNull
    val events: Events?,
) {
    data class Events(
        @field:Valid
        @field:NotNull
        val target: Target?,

        @field:Valid
        @field:NotNull
        val source: Source?,
    ) {
        data class Target(
            @field:NotBlank
            val appEvents: String?,
        )

        data class Source(
            @field:NotBlank
            val createScheduledTaskEvents: String?,

            @field:NotBlank
            val deleteScheduledTaskEvents: String?,
        )
    }
}
