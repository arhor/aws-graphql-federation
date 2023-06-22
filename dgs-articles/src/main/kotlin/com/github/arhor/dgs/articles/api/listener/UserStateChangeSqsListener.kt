package com.github.arhor.dgs.articles.api.listener

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserStateChangeSqsListener {

    @SqsListener("\${application-props.aws.sqs.user-updated-events}")
    fun handleUserUpdate(message: Message<String>) {
        logger.debug("Processing event: {}", message)
    }

    @SqsListener("\${application-props.aws.sqs.user-deleted-events}")
    fun handleUserDelete(message: Message<String>) {
        logger.debug("Processing event: {}", message)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserStateChangeSqsListener::class.java)
    }
}
