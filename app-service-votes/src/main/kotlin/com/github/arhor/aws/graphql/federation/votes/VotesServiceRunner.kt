package com.github.arhor.aws.graphql.federation.votes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UsersServiceRunner

fun main(args: Array<String>) {
    runApplication<UsersServiceRunner>(*args)
}
