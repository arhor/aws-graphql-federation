package com.github.arhor.aws.graphql.federation.scheduler.api.scheduler

import com.github.arhor.aws.graphql.federation.scheduler.service.ScheduledEventService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledEventsProcessor(
    private val scheduledEventService: ScheduledEventService,
) {

    @Scheduled(cron = "*/15 * * * * *")
    fun processScheduledEvent() {
        scheduledEventService.publishMatureEvents()
    }
}
