package com.github.arhor.aws.graphql.federation.posts.service.event.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.service.event.PostEventPublisher
import com.github.arhor.aws.graphql.federation.tracing.TRACING_ID_KEY
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PostEventPublisherImpl(
    private val appProps: AppProps,
    private val sns: SnsOperations,
) : PostEventPublisher {

    @Retryable(
        include = [
            MessagingException::class,
        ],
        backoff = Backoff(
            delayExpression = "\${app-props.retry.delay:}",
            multiplierExpression = "\${app-props.retry.multiplier:}",
        ),
        maxAttemptsExpression = "\${app-props.retry.max-attempts:}",
    )
    override fun publish(event: PostEvent, traceId: UUID) {
        val snsTopicName = appProps.aws.sns.postEvents
        val notification = SnsNotification(event, event.attributes(TRACING_ID_KEY to traceId))

        sns.sendNotification(snsTopicName, notification)
    }
}
