package com.github.arhor.aws.graphql.federation.users.service.events.impl

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import com.github.arhor.aws.graphql.federation.users.service.events.OutboxEventPublisher
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class OutboxEventPublisherImpl(
    private val appProps: AppProps,
    private val sns: SnsOperations,
) : OutboxEventPublisher {

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
    override fun publish(outboxEvent: OutboxEventEntity) {
        val snsTopicName = determineSnsTopicName(outboxEvent.type)
        val notification = SnsNotification(outboxEvent.payload, outboxEvent.headers)

        sns.sendNotification(snsTopicName, notification)
    }

    private fun determineSnsTopicName(outboxEventType: String): String = when (outboxEventType) {
        UserEvent.USER_EVENT_DELETED -> {
            appProps.aws.sns.userEvents
        }

        else -> {
            throw UnsupportedOperationException(
                "Unsupported outbox event type: [$outboxEventType]"
            )
        }
    }
}
