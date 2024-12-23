package com.github.arhor.aws.graphql.federation.scheduler.service

import com.github.arhor.aws.graphql.federation.common.event.ScheduledEvent

interface ScheduledEventService {
    fun storeScheduledEvent(event: ScheduledEvent.Created)
    fun clearScheduledEvent(event: ScheduledEvent.Deleted)
    fun publishMatureEvents()
}
