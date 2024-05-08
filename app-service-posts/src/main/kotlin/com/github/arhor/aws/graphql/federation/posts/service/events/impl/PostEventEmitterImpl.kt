package com.github.arhor.aws.graphql.federation.posts.service.events.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.service.events.PostEventEmitter
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class PostEventEmitterImpl(
    private val sns: SnsOperations,
    private val appProps: AppProps,
) : PostEventEmitter {

    @Retryable(retryFor = [MessagingException::class])
    override fun emit(event: PostEvent) {
        val snsTopicName = appProps.aws.sns.postEvents
        val notification = SnsNotification(event, event.attributes())

        sns.sendNotification(snsTopicName, notification)
    }
}
