package com.github.arhor.dgs.comments.api.listener

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PostChangeSqsListener {

    @SqsListener("\${app-props.aws.sqs.post-updates}")
    fun handleUserUpdates(change: PostChange.Updated) {
        logger.debug("Processing update event: {}", change)
    }

    @SqsListener("\${app-props.aws.sqs.post-deletes}")
    fun handleUserDeletes(change: PostChange.Deleted) {
        logger.debug("Processing delete event: {}", change)
    }

    sealed interface PostChange {
        data class Updated(val id: Long) : PostChange
        data class Deleted(val id: Long) : PostChange
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PostChangeSqsListener::class.java)
    }
}
