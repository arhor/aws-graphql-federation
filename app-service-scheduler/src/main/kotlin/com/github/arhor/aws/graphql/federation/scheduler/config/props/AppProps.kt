package com.github.arhor.aws.graphql.federation.scheduler.config.props

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.math.BigDecimal

@Validated
@ConfigurationProperties("app-props")
data class AppProps(
    @field:Valid
    @field:NotNull
    val events: Events?,

    @field:Valid
    @field:NotNull
    val retry: Retry?,
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
            val handleScheduledEvent: String?,
        )
    }

    data class Retry(
        @field:Min(1)
        @field:Max(10_000)
        val minInterval: Long = 1000,

        @field:Min(1)
        @field:Max(10_000)
        val maxInterval: Long = 10_000,

        @field:Min(1)
        @field:Max(10)
        val maxAttempts: Int = 3,

        @field:DecimalMin((1.2).toString())
        @field:DecimalMax((3.0).toString())
        val multiplier: BigDecimal = (1.5).toBigDecimal(),
    )
}
