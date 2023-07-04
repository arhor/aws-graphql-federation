package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.lib.event.UserEvent
import com.github.arhor.dgs.users.config.props.AppProps
import com.github.arhor.dgs.users.service.UserEventEmitter
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class UserEventEmitterImpl(
    private val snsOperations: SnsOperations,
    appProps: AppProps,
) : UserEventEmitter {

    private val userEventsTopic = appProps.aws.sns.userEvents

    @Retryable(retryFor = [MessagingException::class])
    override fun emit(event: UserEvent) {
        snsOperations.sendNotification(
            userEventsTopic,
            SnsNotification(
                event,
                event.attributes()
            )
        )
    }
}
