package com.github.arhor.aws.graphql.federation.scheduledEvents.service

import com.github.arhor.aws.graphql.federation.common.event.ScheduledEvent

interface ScheduledEventService {
    fun storeCreatedScheduledEvent(event: ScheduledEvent.Created)
    fun clearCreatedScheduledEvent(event: ScheduledEvent.Deleted)
    fun publishMatureEvents()
}
