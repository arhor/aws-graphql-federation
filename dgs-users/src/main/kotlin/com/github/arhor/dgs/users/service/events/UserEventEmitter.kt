package com.github.arhor.dgs.users.service.events

import com.github.arhor.dgs.lib.event.UserEvent

interface UserEventEmitter {

    fun emit(event: UserEvent)
}
