package com.github.arhor.aws.graphql.federation.scheduledEvents

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalDateTime
import java.time.ZonedDateTime

@SpringBootApplication
class ScheduledEventsServiceRunner

fun main(args: Array<String>) {

    ZonedDateTime.now().toInstant()

    runApplication<ScheduledEventsServiceRunner>(*args)
}
