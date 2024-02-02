package com.github.arhor.aws.graphql.federation.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UsersServiceRunner

fun main(args: Array<String>) {
    runApplication<UsersServiceRunner>(*args)
}
