package com.github.arhor.aws.graphql.federation.users.api.scheduler

import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxMessageProcessor(
    private val outboxMessageService: OutboxMessageService,
) {
    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    fun processOutboxMessages() {
        outboxMessageService.releaseOutboxMessages(MESSAGES_BATCH_SIZE)
    }

    companion object {
        private const val MESSAGES_BATCH_SIZE = 50
    }
}
