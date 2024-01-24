package com.github.arhor.aws.graphql.federation.users

import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@EnableRetry
@EnableScheduling
@EnableConfigurationProperties(AppProps::class)
@SpringBootApplication
class UsersServiceRunner

fun main(args: Array<String>) {
    runApplication<UsersServiceRunner>(*args)
}
