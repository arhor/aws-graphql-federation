package com.github.arhor.aws.graphql.federation.posts.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app-props")
data class AppProps(
    val aws: Aws,
) {
    data class Aws(
        val sns: Sns,
        val s3: S3,
    ) {
        data class Sns(
            val postEvents: String,
        )

        data class S3(
            val bannersBucketName: String,
        )
    }
}
