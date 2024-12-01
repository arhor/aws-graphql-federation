package com.github.arhor.aws.graphql.federation.scheduledEvents.service.impl

import com.github.arhor.aws.graphql.federation.common.event.AppEvent
import com.github.arhor.aws.graphql.federation.common.event.ScheduledEvent
import com.github.arhor.aws.graphql.federation.common.invokeAll
import com.github.arhor.aws.graphql.federation.common.withPermit
import com.github.arhor.aws.graphql.federation.scheduledEvents.config.props.AppProps
import com.github.arhor.aws.graphql.federation.scheduledEvents.data.model.ScheduledEventEntity
import com.github.arhor.aws.graphql.federation.scheduledEvents.data.repository.ScheduledEventRepository
import com.github.arhor.aws.graphql.federation.scheduledEvents.service.ScheduledEventService
import com.github.arhor.aws.graphql.federation.starter.core.time.TimeOperations
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.slf4j.LoggerFactory
import org.springframework.retry.RetryOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future.State
import java.util.concurrent.Semaphore
import kotlin.time.Duration.Companion.seconds

@Service
@Transactional
class ScheduledEventServiceImpl(
    appProps: AppProps,
    private val scheduledEventRepository: ScheduledEventRepository,
    private val snsRetryOperations: RetryOperations,
    private val sns: SnsOperations,
    private val timeOperations: TimeOperations,
) : ScheduledEventService {

    private val appEventsTopicName = appProps.events!!.target!!.appEvents!!
    private val vExecutor = Executors.newVirtualThreadPerTaskExecutor()
    private val semaphore = Semaphore(CONCURRENT_EVENTS, true)

    override fun storeScheduledEvent(event: ScheduledEvent.Created) {
        scheduledEventRepository.save(
            ScheduledEventEntity(
                event.id,
                event.type,
                data = event.data,
                publishDateTime = timeOperations.convertToLocalDateTime(event.whenToPublish),
                shouldBePersisted = true,
            )
        )
    }

    override fun clearScheduledEvent(event: ScheduledEvent.Deleted) {
        scheduledEventRepository.deleteById(event.id)
    }

    override fun publishMatureEvents() {
        val currDateTime = timeOperations.currentLocalDateTime()
        val sentEventIds =
            scheduledEventRepository.findEventsByReleaseDateTimeBefore(before = currDateTime, limit = 50)
                .ifEmpty { return }
                .map { createSnsPublicationTask(event = it) }
                .let { vExecutor.invokeAll(tasks = it, timeout = SNS_PUBLICATION_TIMEOUT) }
                .filter { it.state() == State.SUCCESS }
                .map { it.get() }

        scheduledEventRepository.deleteAllById(sentEventIds)
    }

    private fun createSnsPublicationTask(event: ScheduledEventEntity): Callable<UUID> {
        val eventId = event.id
        val messageData = event.data

        require(messageData.isNotEmpty())

        val notification = SnsNotification(
            messageData,
            AppEvent.attributes(
                type = event.type,
                traceId = UUID.randomUUID().toString(),
                idempotencyKey = eventId.toString(),
            )
        )
        return Callable {
            semaphore.withPermit(timeout = 30.seconds) {
                snsRetryOperations.execute<Unit, Throwable> {
                    sns.sendNotification(appEventsTopicName, notification)
                }
                eventId
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java.enclosingClass)
        private val SNS_PUBLICATION_TIMEOUT = 10.seconds
        private const val CONCURRENT_EVENTS = 10
    }
}
