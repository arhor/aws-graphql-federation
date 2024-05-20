package com.github.arhor.aws.graphql.federation.users.config.props

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
    ) {
        data class Sns(
            @field:NotBlank
            val userEvents: String?,
        )
    }
}
