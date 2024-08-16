package com.github.arhor.aws.graphql.federation.scheduledEvents.infrastructure.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledEventsProcessor {

    @Scheduled(cron = "\${app-props.scheduled-events-processing-cron}")
    fun processScheduledEvent() {
        TODO("Implement!")
    }
}
