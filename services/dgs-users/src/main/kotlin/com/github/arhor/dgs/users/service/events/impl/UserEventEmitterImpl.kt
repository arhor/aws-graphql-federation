package com.github.arhor.dgs.users.service.events.impl

import com.github.arhor.dgs.lib.event.UserEvent
import com.github.arhor.dgs.users.config.props.AppProps
import com.github.arhor.dgs.users.service.events.UserEventEmitter
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class UserEventEmitterImpl(
    private val appProps: AppProps,
    private val sns: SnsOperations,
) : UserEventEmitter {

    @Retryable(
        include = [
            MessagingException::class,
        ],
        backoff = Backoff(
            delayExpression = "\${app-props.retry.delay:1000}",
            multiplierExpression = "\${app-props.retry.multiplier:0}",
        ),
        maxAttemptsExpression = "\${app-props.retry.max-attempts:3}",
    )
    override fun emit(event: UserEvent) {
        val snsTopicName = appProps.aws.sns.userEvents
        val notification = SnsNotification(event, event.attributes())

        sns.sendNotification(snsTopicName, notification)
    }
}
