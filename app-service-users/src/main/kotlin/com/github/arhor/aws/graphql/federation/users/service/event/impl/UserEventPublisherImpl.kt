package com.github.arhor.aws.graphql.federation.users.service.event.impl

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.service.event.UserEventPublisher
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserEventPublisherImpl(
    private val appProps: AppProps,
    private val sns: SnsOperations,
) : UserEventPublisher {

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
    override fun publish(event: UserEvent, idempotencyId: UUID) {
        val snsTopicName = appProps.aws.sns.userEvents
        val notification = SnsNotification(event, event.attributes(idempotencyId))

        sns.sendNotification(snsTopicName, notification)
    }
}