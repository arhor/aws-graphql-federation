package com.github.arhor.aws.graphql.federation.scheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SchedulerServiceRunner

fun main(args: Array<String>) {
    runApplication<SchedulerServiceRunner>(*args)
}
