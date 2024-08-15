package com.github.arhor.aws.graphql.federation.scheduledTasks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScheduledTasksServiceRunner

fun main(args: Array<String>) {
    runApplication<ScheduledTasksServiceRunner>(*args)
}
