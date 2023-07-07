package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.UserService
import com.github.arhor.dgs.lib.event.UserEvent
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserEventsSqsListener @Autowired constructor(
    private val userService: UserService,
) {

    @SqsListener("\${app-props.aws.sqs.user-created-events}")
    fun handleUserCreatedEvent(event: UserEvent.Created) {
        userService.createUser(userId = event.id)
    }

    @SqsListener("\${app-props.aws.sqs.user-deleted-events}")
    fun handleUserDeletedEvent(event: UserEvent.Deleted) {
        userService.deleteUser(userId = event.id)
    }
}
