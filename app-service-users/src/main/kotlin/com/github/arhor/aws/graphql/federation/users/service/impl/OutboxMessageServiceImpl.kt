package com.github.arhor.aws.graphql.federation.users.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.common.invokeAll
import com.github.arhor.aws.graphql.federation.common.withPermit
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
import java.util.concurrent.Future.State
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
                data = objectMapper.convertValue(event, outboxMessageDataTypeRef),
                traceId = useContextAttribute(Attributes.TRACING_ID),
            )
        )
    }

    @Transactional
    override fun releaseOutboxMessagesOfType(eventType: UserEvent.Type) {
        val sentMessageIds =
            outboxMessageRepository.findOldestMessagesWithLock(type = eventType.code, limit = MESSAGES_BATCH_SIZE)
                .also { if (it.isEmpty()) return }
                .map { createSnsPublicationTask(message = it, type = eventType.type.java) }
                .let { vExecutor.invokeAll(tasks = it, timeout = snsPublicationTimeout) }
                .filter { it.state() == State.SUCCESS }
                .map { it.get() }

        outboxMessageRepository.deleteAllById(sentMessageIds)
    }

    private fun createSnsPublicationTask(message: OutboxMessageEntity, type: Class<out UserEvent>): Callable<UUID> {
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
        return Callable {
            semaphore.withPermit(timeout = 30.seconds) {
                snsRetryOperations.execute<Unit, Throwable> {
                    sns.sendNotification(appEventsTopicName, notification)
                }
                messageId
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
        private val outboxMessageDataTypeRef = object : TypeReference<Map<String, Any?>>() {}
        private val snsPublicationTimeout = 10.seconds

        private const val MESSAGES_BATCH_SIZE = 50
        private const val CONCURRENT_MESSAGES = 10
    }
}
