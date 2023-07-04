package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.UserService
import com.github.arhor.dgs.lib.event.UserEvent
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserEventsSqsListener @Autowired constructor(
    private val userService: UserService,
) {

    @SqsListener("\${app-props.aws.sqs.user-created-events}")
    fun handleUserUpdatedEvent(event: UserEvent.Created) {
        logger.debug("Processing user-created event: {}", event)
        userService.createUser(userId = event.id)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events}")
    fun handleUserDeletedEvent(event: UserEvent.Deleted) {
        logger.debug("Processing user-deleted event: {}", event)
        userService.deleteUser(userId = event.id)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserEventsSqsListener::class.java)
    }
}
