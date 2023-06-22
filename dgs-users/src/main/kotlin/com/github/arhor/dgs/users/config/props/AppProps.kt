package com.github.arhor.dgs.users.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application-props")
data class AppProps(
    val aws: Aws,
) {
    data class Aws(
        val sns: Sns,
    ) {
        data class Sns(
            val userUpdatedEvents: String,
            val userDeletedEvents: String,
        )
    }
}
