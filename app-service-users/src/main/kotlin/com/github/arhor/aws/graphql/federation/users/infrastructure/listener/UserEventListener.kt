package com.github.arhor.aws.graphql.federation.users.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Trace
@Component
class UserEventListener(
    private val outboxMessageService: OutboxMessageService,
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun onUserEvent(event: UserEvent) {
        outboxMessageService.storeToOutboxMessages(event)
    }
}
