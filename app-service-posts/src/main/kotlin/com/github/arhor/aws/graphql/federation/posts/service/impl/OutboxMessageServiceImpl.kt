package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.data.model.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.posts.service.OutboxMessageService
import com.github.arhor.aws.graphql.federation.starter.tracing.Attributes
import com.github.arhor.aws.graphql.federation.starter.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.starter.tracing.useContextAttribute
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.retry.RetryOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OutboxMessageServiceImpl(
    appProps: AppProps,
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val snsRetryOperations: RetryOperations,
    private val sns: SnsOperations,
) : OutboxMessageService {

    private val appEventsTopicName = appProps.aws!!.sns!!.appEvents!!

    @Trace
    @Transactional(propagation = Propagation.MANDATORY)
    override fun storeAsOutboxMessage(event: PostEvent) {
        outboxMessageRepository.save(
            OutboxMessageEntity(
                type = event.type(),
                data = objectMapper.convertValue(event, OutboxMessageDataTypeRef),
                traceId = useContextAttribute(Attributes.TRACING_ID),
            )
        )
    }

    @Transactional
    override fun releaseOutboxMessagesOfType(eventType: PostEvent.Type) {
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
                idempotencyKey = message.id!!,
            )
        }
    }

    private fun publishToSns(event: PostEvent, traceId: UUID, idempotencyKey: UUID) {
        val notification = SnsNotification(
            event,
            event.attributes(
                TRACING_ID_KEY to traceId.toString(),
                IDEMPOTENT_KEY to idempotencyKey.toString(),
            )
        )
        snsRetryOperations.execute<Unit, Throwable> {
            sns.sendNotification(appEventsTopicName, notification)
        }
    }

    companion object {
        private const val DEFAULT_EVENTS_BATCH_SIZE = 50

        internal object OutboxMessageDataTypeRef : TypeReference<Map<String, Any?>>()
    }
}
