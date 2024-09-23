package com.github.arhor.aws.graphql.federation.users.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.async
import com.github.arhor.aws.graphql.federation.common.await
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.common.use
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
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import kotlin.time.Duration.Companion.seconds

private typealias PublishingData = Pair<UUID, SnsNotification<UserEvent>>

@Service
class OutboxMessageServiceImpl(
    appProps: AppProps,
    private val objectMapper: ObjectMapper,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val snsRetryOperations: RetryOperations,
    private val sns: SnsOperations,
) : OutboxMessageService {

    private val appEventsTopicName = appProps.events!!.target!!.appEvents!!
    private val vExecutor = Executors.newVirtualThreadPerTaskExecutor()
    private val semaphore = Semaphore(CONCURRENT_MESSAGES)

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
        outboxMessageRepository
            .findOldestMessagesWithLock(type = eventType.code, limit = MESSAGES_BATCH_SIZE)
            .also { if (it.isEmpty()) return }
            .map { createSnsNotification(message = it, type = eventType.type.java) }
            .async(parallelism = CONCURRENT_MESSAGES, timeout = 10.seconds, action = ::tryPublishSnsNotification)
            .await(ignoreExceptions = true)
            .let { sentMessageIds -> outboxMessageRepository.deleteAllById(sentMessageIds) }
    }

    private fun createSnsNotification(
        message: OutboxMessageEntity,
        type: Class<out UserEvent>,
    ): Pair<UUID, SnsNotification<UserEvent>> {
        val messageId = message.id
        val messageData = message.data

        require(messageId != null)
        require(messageData.isNotEmpty())

        val notification = objectMapper.convertValue(messageData, type).let {
            SnsNotification(
                it,
                it.attributes(
                    TRACING_ID_KEY to message.traceId.toString(),
                    IDEMPOTENT_KEY to messageId.toString(),
                )
            )
        }
        return messageId to notification
    }

    private fun tryPublishSnsNotification(data: Pair<UUID, SnsNotification<UserEvent>>): UUID? {
        val (messageId, notification) = data
        return try {
            snsRetryOperations.execute<Unit, Throwable> {
                sns.sendNotification(appEventsTopicName, notification)
            }
            messageId
        } catch (e: Exception) {
            logger.error("SNS notification '{}' publication failed: {}", notification, messageId, e)
            null
        }
    }

    private fun createSnsPublicationTask(message: OutboxMessageEntity, type: Class<out UserEvent>): Callable<UUID?> {
        val (messageId, notification) = createSnsNotification(message, type)

        return Callable {
            try {
                semaphore.use {
                    snsRetryOperations.execute<Unit, Throwable> {
                        sns.sendNotification(appEventsTopicName, notification)
                    }
                }
                messageId
            } catch (e: Exception) {
                logger.error("SNS notification '{}' publication failed: {}", notification, messageId, e)
                null
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
        private const val MESSAGES_BATCH_SIZE = 50
        private const val MESSAGE_PUB_TIMEOUT = 10L
        private const val CONCURRENT_MESSAGES = 10

        private object OutboxMessageDataTypeRef : TypeReference<Map<String, Any?>>()
    }
}
