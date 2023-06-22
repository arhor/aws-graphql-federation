package com.github.arhor.dgs.users

import com.github.arhor.dgs.users.config.props.AppProps
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.retry.annotation.EnableRetry
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@EnableRetry
@EnableConfigurationProperties(AppProps::class)
@SpringBootApplication
class Main {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
