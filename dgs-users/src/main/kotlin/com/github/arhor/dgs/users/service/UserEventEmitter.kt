package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.lib.event.UserEvent

interface UserEventEmitter {

    fun emit(event: UserEvent)
}
