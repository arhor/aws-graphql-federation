package com.github.arhor.aws.graphql.federation.users.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.tracing.Attributes
import com.github.arhor.aws.graphql.federation.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.tracing.useContextAttribute
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.retry.RetryOperations
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class OutboxMessageServiceImpl(
    private val appProps: AppProps,
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val snsRetryOperations: RetryOperations,
    private val sns: SnsOperations,
) : OutboxMessageService {

    override fun storeAsOutboxMessage(event: UserEvent) {
        outboxMessageRepository.save(
            OutboxMessageEntity(
                type = event.type(),
                data = objectMapper.convertValue(event),
                traceId = useContextAttribute(Attributes.TRACING_ID),
            )
        )
    }

    override fun releaseOutboxMessagesOfType(eventType: UserEvent.Type) {
        val outboxMessages =
            outboxMessageRepository.dequeueOldest(
                messageType = eventType.code,
                messagesNum = DEFAULT_EVENTS_BATCH_SIZE,
            )

        for (message in outboxMessages) {
            val event = objectMapper.convertValue(message.data, eventType.type.java)

            publishToSns(
                event = event,
                traceId = message.traceId,
                idempotentKey = message.id!!,
            )
        }
    }

    private fun publishToSns(event: UserEvent, traceId: UUID, idempotentKey: UUID) {
        val snsTopicName = appProps.aws!!.sns!!.userEvents!!
        val notification = SnsNotification(
            event,
            event.attributes(
                TRACING_ID_KEY to traceId.toString(),
                IDEMPOTENT_KEY to idempotentKey.toString(),
            )
        )
        snsRetryOperations.execute<Unit, Throwable> {
            sns.sendNotification(snsTopicName, notification)
        }
    }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 50
    }
}
