package com.github.arhor.dgs.articles.api.listener

import com.github.arhor.dgs.lib.HEADER_PAYLOAD_TYPE
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserStateChangeSqsListener {

    @SqsListener("\${app-props.aws.sqs.user-state-changes}")
    fun handleUserStateChange(message: Message<String>) {
        when (val payloadType = message.headers[HEADER_PAYLOAD_TYPE]) {
            "UserStateChange.Updated" -> processUpdate(message.payload)
            "UserStateChange.Deleted" -> processDelete(message.payload)
            else -> throw IllegalArgumentException("Unsupported payload type: $payloadType")
        }
    }

    private fun processUpdate(event: String) {
        logger.debug("Processing update event: {}", event)
    }

    private fun processDelete(event: String) {
        logger.debug("Processing delete event: {}", event)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserStateChangeSqsListener::class.java)
    }
}
