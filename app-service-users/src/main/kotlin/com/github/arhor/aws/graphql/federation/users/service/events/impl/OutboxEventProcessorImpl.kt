package com.github.arhor.aws.graphql.federation.users.service.events.impl

import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxEventRepository
import com.github.arhor.aws.graphql.federation.users.service.events.OutboxEventProcessor
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Component
class OutboxEventProcessorImpl(
    private val appProps: AppProps,
    private val sns: SnsOperations,
    private val outboxEventRepository: OutboxEventRepository,
) : OutboxEventProcessor {

    @Scheduled(
        fixedDelay = 5,
        timeUnit = TimeUnit.SECONDS,
    )
    @Retryable(
        include = [
            MessagingException::class,
        ],
        backoff = Backoff(
            delayExpression = "\${app-props.retry.delay}",
            multiplierExpression = "\${app-props.retry.multiplier}",
        ),
        maxAttemptsExpression = "\${app-props.retry.max-attempts}",
    )
    @Transactional
    override fun processOutboxEvents() {
        for (outboxEvent in outboxEventRepository.dequeueOldest(eventsNum = 50)) {
            val snsTopicName = appProps.aws.sns.userEvents
            val notification = SnsNotification(outboxEvent.payload, outboxEvent.headers)

            sns.sendNotification(snsTopicName, notification)
            outboxEventRepository.delete(outboxEvent)
        }
    }
}
