package com.github.arhor.aws.graphql.federation.posts.config.props

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "app-props")
data class AppProps(
    @field:Valid
    @field:NotNull
    val aws: Aws?,
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
            val postEvents: String?,
        )

        data class Sqs(
            @field:NotBlank
            val userCreatedEvents: String?,

            @field:NotBlank
            val userDeletedEvents: String?,
        )
    }
}
