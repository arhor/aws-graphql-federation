@file:Suppress("MemberVisibilityCanBePrivate", "Unused")

package com.github.arhor.aws.graphql.federation.posts.config.props

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
@ConfigurationProperties(prefix = AppProps.PATH)
data class AppProps(
    @field:Valid
    @field:NotNull
    val aws: Aws?,

    @field:Valid
    @field:NotNull
    val retry: Retry?,
) {
    data class Aws(
        @field:Valid
        @field:NotNull
        val sns: Sns?,

        @field:Valid
        @field:NotNull
        val sqs: Sqs?,
    ) {
        data class Sns(
            @field:NotBlank
            val appEvents: String?,
        ) {
            companion object {
                const val PATH = "${AppProps.Aws.PATH}.sns"
                const val APP_EVENTS = "$PATH.app-events"
            }
        }

        data class Sqs(
            @field:NotBlank
            val syncPostsOnUserCreatedEvent: String?,

            @field:NotBlank
            val syncPostsOnUserDeletedEvent: String?,
        ) {
            companion object {
                const val PATH = "${AppProps.Aws.PATH}.sqs"
                const val SYNC_POSTS_ON_USER_CREATED_EVENT = "$PATH.sync-posts-on-user-created-event"
                const val SYNC_POSTS_ON_USER_DELETED_EVENT = "$PATH.sync-posts-on-user-deleted-event"
            }
        }

        companion object {
            const val PATH = "${AppProps.PATH}.aws"
        }
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
    ) {
        companion object {
            const val PATH = "${AppProps.PATH}.retry"
            const val MIN_INTERVAL = "$PATH.min-interval"
            const val MAX_INTERVAL = "$PATH.max-interval"
            const val MAX_ATTEMPTS = "$PATH.max-attempts"
            const val MULTIPLIER = "$PATH.multiplier"
        }
    }

    companion object {
        const val PATH = "app-props"
    }
}
