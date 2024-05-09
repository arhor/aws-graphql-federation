package com.github.arhor.aws.graphql.federation.users.service.event

import com.github.arhor.aws.graphql.federation.common.event.UserEvent

interface UserEventPublisher {

    fun publish(userEvent: UserEvent)
}
