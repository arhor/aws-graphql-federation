package com.github.arhor.aws.graphql.federation.users.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.starter.tracing.Attributes
import com.github.arhor.aws.graphql.federation.starter.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.starter.tracing.useContextAttribute
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.data.model.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.slf4j.LoggerFactory
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

    private val appEventsTopicName = appProps.events!!.target!!.appEvents!!

    @Trace
    @Transactional(propagation = Propagation.MANDATORY)
    override fun storeAsOutboxMessage(event: UserEvent) {
        outboxMessageRepository.save(
            OutboxMessageEntity(
                type = event.type(),
                data = objectMapper.convertValue(event, OutboxMessageDataTypeRef),
                traceId = useContextAttribute(Attributes.TRACING_ID),
            )
        )
    }

    @Transactional
    override fun releaseOutboxMessagesOfType(eventType: UserEvent.Type) {
        val outboxMessages = outboxMessageRepository.findOldestMessagesWithLock(
            type = eventType.code,
            limit = DEFAULT_EVENTS_BATCH_SIZE,
        )
        val numberOfMessages = outboxMessages.size.also {
            if (it == 0) {
                return
            }
        }
        val sentMessages = ArrayList<OutboxMessageEntity>(numberOfMessages)

        for (message in outboxMessages) {
            val event = objectMapper.convertValue(message.data, eventType.type.java)
            val isSuccess = tryPublishToSns(
                event = event,
                traceId = message.traceId,
                idempotencyKey = message.id!!,
            )
            if (isSuccess) {
                sentMessages.add(message)
            }
        }
        outboxMessageRepository.deleteAll(sentMessages)
    }

    private fun tryPublishToSns(event: UserEvent, traceId: UUID, idempotencyKey: UUID): Boolean {
        val notification = SnsNotification(
            event,
            event.attributes(
                TRACING_ID_KEY to traceId.toString(),
                IDEMPOTENT_KEY to idempotencyKey.toString(),
            )
        )
        return try {
            snsRetryOperations.execute<Unit, Throwable> {
                sns.sendNotification(appEventsTopicName, notification)
            }
            true
        } catch (e: Exception) {
            logger.error("Event {} publication failed: {}", event.type(), event, e)
            false
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
        private const val DEFAULT_EVENTS_BATCH_SIZE = 50

        private object OutboxMessageDataTypeRef : TypeReference<Map<String, Any?>>()
    }
}
