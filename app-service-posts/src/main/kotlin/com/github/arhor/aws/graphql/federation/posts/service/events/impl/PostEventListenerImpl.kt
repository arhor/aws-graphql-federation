package com.github.arhor.aws.graphql.federation.posts.service.events.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.posts.service.events.PostEventListener
import com.github.arhor.aws.graphql.federation.tracing.Trace
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional

@Trace
@Component
class PostEventListenerImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
) : PostEventListener {

    @EventListener(PostEvent::class)
    @Transactional(propagation = MANDATORY)
    override fun onPostEvent(event: PostEvent) {
        outboxMessageRepository.save(
            OutboxMessageEntity(
                type = event.type(),
                data = objectMapper.convertValue(event),
            )
        )
    }
}
