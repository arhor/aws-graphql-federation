package com.github.arhor.aws.graphql.federation.scheduledEvents.data.repository

import com.github.arhor.aws.graphql.federation.scheduledEvents.data.model.ScheduledEventEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.Instant
import java.util.UUID

interface ScheduledEventRepository : CrudRepository<ScheduledEventEntity, UUID> {

    @Query(name = "ScheduledEventEntity.findEventsByReleaseTimestampBefore")
    fun findEventsByReleaseTimestampBefore(
        limit: Int,
        before: Instant,
        withLock: Boolean = false,
    ): List<ScheduledEventEntity>
}
