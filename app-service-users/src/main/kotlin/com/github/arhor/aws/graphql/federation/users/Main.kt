package com.github.arhor.aws.graphql.federation.users

import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@EnableConfigurationProperties(AppProps::class)
@SpringBootApplication
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
