package com.github.arhor.aws.graphql.federation.scheduledEvents

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScheduledEventsServiceRunner

fun main(args: Array<String>) {
    runApplication<ScheduledEventsServiceRunner>(*args)
}
