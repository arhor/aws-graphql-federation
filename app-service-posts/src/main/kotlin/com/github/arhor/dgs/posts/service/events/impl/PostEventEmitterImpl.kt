package com.github.arhor.dgs.posts.service.events.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.dgs.posts.config.props.AppProps
import com.github.arhor.dgs.posts.service.events.PostEventEmitter
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class PostEventEmitterImpl @Autowired constructor(
    private val snsOperations: SnsOperations,
    appProps: AppProps,
) : PostEventEmitter {

    private val postEventsTopic = appProps.aws.sns.postEvents

    @Retryable(retryFor = [MessagingException::class])
    override fun emit(event: PostEvent) {
        snsOperations.sendNotification(
            postEventsTopic,
            SnsNotification(
                event,
                event.attributes()
            )
        )
    }
}
