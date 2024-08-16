package com.github.arhor.aws.graphql.federation.scheduledEvents.service.impl

import com.github.arhor.aws.graphql.federation.common.event.ScheduledEvent
import com.github.arhor.aws.graphql.federation.scheduledEvents.data.repository.ScheduledEventRepository
import com.github.arhor.aws.graphql.federation.scheduledEvents.service.ScheduledEventService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScheduledEventServiceImpl(
    private val scheduledEventRepository: ScheduledEventRepository,
) : ScheduledEventService {

    override fun storeCreatedScheduledEvent(event: ScheduledEvent.Created) {
        TODO("Not yet implemented")
    }

    override fun clearCreatedScheduledEvent(event: ScheduledEvent.Deleted) {
        TODO("Not yet implemented")
    }

    override fun publishMatureEvents() {
        TODO("Not yet implemented")
    }
}
