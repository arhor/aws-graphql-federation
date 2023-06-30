package com.github.arhor.dgs.comments.api.listener

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserChangeSqsListener {

    @SqsListener("\${app-props.aws.sqs.user-updates}")
    fun handleUserUpdates(change: UserChange.Updated) {
        logger.debug("Processing update event: {}", change)
    }

    @SqsListener("\${app-props.aws.sqs.user-deletes}")
    fun handleUserDeletes(change: UserChange.Deleted) {
        logger.debug("Processing delete event: {}", change)
    }

    sealed interface UserChange {
        data class Updated(val id: Long) : UserChange
        data class Deleted(val id: Long) : UserChange
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserChangeSqsListener::class.java)
    }
}
