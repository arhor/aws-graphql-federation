package com.github.arhor.aws.graphql.federation.posts.infrastructure.scheduler

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.service.OutboxMessageService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class PostEventProcessor(
    private val outboxMessageService: OutboxMessageService,
) {

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    fun processPostCreatedEvents() {
        outboxMessageService.releaseOutboxMessagesOfType(PostEvent.Type.POST_EVENT_CREATED)
    }

    @Scheduled(cron = "\${app-props.outbox-messages-processing-cron}")
    @Transactional(propagation = Propagation.REQUIRED)
    fun processPostDeletedEvents() {
        outboxMessageService.releaseOutboxMessagesOfType(PostEvent.Type.POST_EVENT_DELETED)
    }
}
