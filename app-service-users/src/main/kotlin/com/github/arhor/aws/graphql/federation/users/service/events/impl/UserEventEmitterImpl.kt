package com.github.arhor.aws.graphql.federation.users.service.events.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxEventRepository
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventEmitter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional

@Trace
@Component
class UserEventEmitterImpl(
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) : UserEventEmitter {

    @Transactional(propagation = MANDATORY)
    override fun emit(event: UserEvent) {
        outboxEventRepository.save(
            OutboxEventEntity(
                type = event.type(),
                payload = objectMapper.convertValue(event),
                headers = event.attributes(),
            )
        )
    }
}
