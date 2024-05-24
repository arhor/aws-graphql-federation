package com.github.arhor.aws.graphql.federation.users.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional

@Trace
@Component
class UserEventListener(
    private val outboxMessageService: OutboxMessageService,
) {

    @EventListener(UserEvent::class)
    @Transactional(propagation = MANDATORY)
    fun onUserEvent(event: UserEvent) {
        outboxMessageService.storeAsOutboxMessage(event)
    }
}
