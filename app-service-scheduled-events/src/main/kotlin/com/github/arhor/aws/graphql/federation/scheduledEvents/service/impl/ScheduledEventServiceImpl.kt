package com.github.arhor.aws.graphql.federation.scheduledEvents.service.impl

import com.github.arhor.aws.graphql.federation.common.event.ScheduledEvent
import com.github.arhor.aws.graphql.federation.scheduledEvents.config.props.AppProps
import com.github.arhor.aws.graphql.federation.scheduledEvents.data.model.ScheduledEventEntity
import com.github.arhor.aws.graphql.federation.scheduledEvents.data.repository.ScheduledEventRepository
import com.github.arhor.aws.graphql.federation.scheduledEvents.service.ScheduledEventService
import com.github.arhor.aws.graphql.federation.starter.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.TRACING_ID_KEY
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.slf4j.LoggerFactory
import org.springframework.retry.RetryOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class ScheduledEventServiceImpl(
    appProps: AppProps,
    private val scheduledEventRepository: ScheduledEventRepository,
    private val snsRetryOperations: RetryOperations,
    private val sns: SnsOperations,
) : ScheduledEventService {

    private val appEventsTopicName = appProps.events!!.target!!.appEvents!!

    override fun storeCreatedScheduledEvent(event: ScheduledEvent.Created) {
        scheduledEventRepository.save(
            ScheduledEventEntity(
                event.id,
                event.type,
                data = event.data,
                createdTimestamp = Instant.now(),
                releaseTimestamp = event.timestamp,
                shouldBePersisted = true,
            )
        )
    }

    override fun clearCreatedScheduledEvent(event: ScheduledEvent.Deleted) {
        scheduledEventRepository.deleteById(event.id)
    }

    override fun publishMatureEvents() {
        val scheduledEvents = scheduledEventRepository.findEventsByReleaseTimestampBefore(
            limit = 50,
            before = Instant.now(),
            withLock = true
        )
        val sentEvents = ArrayList<ScheduledEventEntity>(scheduledEvents.size)

        for (event in scheduledEvents) {
            val isSuccess = tryPublishToSns(
                event = ScheduledEvent.Published(
                    id = event.id,
                    type = event.type,
                    data = event.data,
                ),
                traceId = UUID.randomUUID(),
                idempotencyKey = event.id,
            )
            if (isSuccess) {
                sentEvents.add(event)
            }
        }
        scheduledEventRepository.deleteAll(sentEvents)
    }

    private fun tryPublishToSns(event: ScheduledEvent.Published, traceId: UUID, idempotencyKey: UUID): Boolean {
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
            logger.error("Scheduled event publication failed, ID: '{}'", event.id, e)
            false
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}