package com.github.arhor.aws.graphql.federation.posts.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.service.OutboxMessageService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional

@Trace
@Component
class PostEventListener(
    private val outboxMessageService: OutboxMessageService,
) {

    @EventListener(PostEvent::class)
    @Transactional(propagation = MANDATORY)
    fun onPostEvent(event: PostEvent) {
        outboxMessageService.storeAsOutboxMessage(event)
    }
}
