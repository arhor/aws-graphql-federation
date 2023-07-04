package com.github.arhor.dgs.posts.service.impl

import com.github.arhor.dgs.lib.event.PostEvent
import com.github.arhor.dgs.posts.config.props.AppProps
import com.github.arhor.dgs.posts.service.PostEventEmitter
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class PostEventEmitterImpl(
    private val snsOperations: SnsOperations,
    appProps: AppProps,
) : PostEventEmitter {

    private val postChangesTopic = appProps.aws.sns.postChanges

    @Retryable(retryFor = [MessagingException::class])
    override fun emit(event: PostEvent) {
        snsOperations.sendNotification(
            postChangesTopic,
            SnsNotification(
                event,
                event.attributes()
            )
        )
    }
}
