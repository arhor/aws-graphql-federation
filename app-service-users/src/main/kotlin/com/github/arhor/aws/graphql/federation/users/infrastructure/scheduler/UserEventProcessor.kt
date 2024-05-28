package com.github.arhor.aws.graphql.federation.users.infrastructure.scheduler

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UserEventProcessor(
    private val outboxMessageService: OutboxMessageService,
) {

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    fun processUserCreatedEvents() {
        outboxMessageService.releaseOutboxMessagesOfType(UserEvent.Type.USER_EVENT_CREATED)
    }

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    fun processUserDeletedEvents() {
        outboxMessageService.releaseOutboxMessagesOfType(UserEvent.Type.USER_EVENT_DELETED)
    }
}
