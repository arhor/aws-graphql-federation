package com.github.arhor.dgs.posts.api.listener

import com.github.arhor.dgs.posts.service.UserService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserEventSqsListener @Autowired constructor(
    private val userService: UserService,
) {

    @SqsListener("\${app-props.aws.sqs.user-created-events}")
    fun handleUserCreatedEvent(event: UserEvent.Updated) {
        logger.debug("Processing user-updated event: {}", event)
        userService.createUser(userId = event.id)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events}")
    fun handleUserDeletedEvent(event: UserEvent.Deleted) {
        logger.debug("Processing user-deleted event: {}", event)
        userService.deleteUser(userId = event.id)
    }

    sealed interface UserEvent {
        data class Updated(val id: Long) : UserEvent
        data class Deleted(val id: Long) : UserEvent
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserEventSqsListener::class.java)
    }
}
