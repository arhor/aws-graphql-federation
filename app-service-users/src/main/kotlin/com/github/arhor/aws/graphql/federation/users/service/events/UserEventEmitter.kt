package com.github.arhor.aws.graphql.federation.users.service.events

import com.github.arhor.aws.graphql.federation.common.event.UserEvent

interface UserEventEmitter {

    fun emit(event: UserEvent)
}
