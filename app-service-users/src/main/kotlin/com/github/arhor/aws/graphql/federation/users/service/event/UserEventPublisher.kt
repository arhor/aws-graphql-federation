package com.github.arhor.aws.graphql.federation.users.service.event

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import java.util.UUID

interface UserEventPublisher {

    fun publish(event: UserEvent, idempotencyKey: UUID)
}
