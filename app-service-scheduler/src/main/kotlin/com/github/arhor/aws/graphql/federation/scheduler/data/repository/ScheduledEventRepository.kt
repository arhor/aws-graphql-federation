package com.github.arhor.aws.graphql.federation.scheduler.data.repository

import com.github.arhor.aws.graphql.federation.scheduler.data.model.ScheduledEventEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime
import java.util.UUID

interface ScheduledEventRepository : CrudRepository<ScheduledEventEntity, UUID> {

    @Query(name = "ScheduledEventEntity.findEventsByReleaseDateTimeBefore")
    fun findEventsByReleaseDateTimeBefore(before: LocalDateTime, limit: Int): List<ScheduledEventEntity>
}
