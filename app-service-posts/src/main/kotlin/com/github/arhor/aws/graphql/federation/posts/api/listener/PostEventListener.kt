package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.service.OutboxMessageService
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Trace
@Component
class PostEventListener(
    private val outboxMessageService: OutboxMessageService,
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun onPostEvent(event: PostEvent) {
        outboxMessageService.storeToOutboxMessages(event)
    }
}
