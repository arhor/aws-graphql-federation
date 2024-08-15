package com.github.arhor.aws.graphql.federation.scheduledTasks.infrastructure.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledTaskProcessor {

    @Scheduled(cron = "\${app-props.scheduled-tasks-processing-cron}")
    fun processScheduledTask() {
        TODO("Implement!")
    }
}
