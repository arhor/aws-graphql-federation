package com.github.arhor.dgs.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@SpringBootApplication
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
