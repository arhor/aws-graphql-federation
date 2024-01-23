package com.github.arhor.aws.graphql.federation.posts

import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@EnableConfigurationProperties(AppProps::class)
@SpringBootApplication
class PostsServiceRunner

fun main(args: Array<String>) {
    runApplication<PostsServiceRunner>(*args)
}
