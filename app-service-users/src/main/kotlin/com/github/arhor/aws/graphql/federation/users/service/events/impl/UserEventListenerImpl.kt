package com.github.arhor.aws.graphql.federation.users.service.events.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventListener
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional

@Trace
@Component
class UserEventListenerImpl(
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
) : UserEventListener {

    @Transactional(propagation = MANDATORY)
    @EventListener(UserEvent::class)
    override fun onUserEvent(event: UserEvent) {
        outboxMessageRepository.save(
            OutboxMessageEntity(
                type = event.type(),
                data = objectMapper.convertValue(event),
            )
        )
    }
}
