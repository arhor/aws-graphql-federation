package com.github.arhor.aws.graphql.federation.posts.infrastructure.scheduler

import com.github.arhor.aws.graphql.federation.posts.service.OutboxMessageService
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
