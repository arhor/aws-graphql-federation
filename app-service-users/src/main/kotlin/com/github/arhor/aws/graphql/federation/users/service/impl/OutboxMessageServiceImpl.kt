package com.github.arhor.aws.graphql.federation.users.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.constants.Attributes
import com.github.arhor.aws.graphql.federation.common.event.AppEvent
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.common.invokeAll
import com.github.arhor.aws.graphql.federation.common.withPermit
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
    private val semaphore = Semaphore(CONCURRENT_MESSAGES, true)

    @Trace
    @Transactional(propagation = Propagation.MANDATORY)
    override fun storeToOutboxMessages(event: UserEvent) {
        outboxMessageRepository.save(
            OutboxMessageEntity(
                type = event.type(),
                data = objectMapper.convertValue(event, outboxMessageDataTypeRef),
                traceId = useContextAttribute(Attributes.TRACE_ID),
            )
        )
    }

    @Transactional
    override fun releaseOutboxMessages(limit: Int) {
        val sentMessageIds =
            outboxMessageRepository.findOldestMessagesWithLock(limit)
                .ifEmpty { return }
                .map { createSnsPublicationTask(message = it) }
                .let { vExecutor.invokeAll(tasks = it, timeout = snsPublicationTimeout) }
                .filter { it.state() == State.SUCCESS }
                .map { it.get() }

        outboxMessageRepository.deleteAllById(sentMessageIds)
    }

    private fun createSnsPublicationTask(message: OutboxMessageEntity): Callable<UUID> {
        val messageId = message.id
        val messageData = message.data

        require(messageId != null)
        require(messageData.isNotEmpty())

        val notification = SnsNotification(
            messageData,
            AppEvent.attributes(
                type = message.type,
                traceId = message.traceId.toString(),
            )
        )
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

        private const val CONCURRENT_MESSAGES = 10
    }
}
