package com.github.arhor.aws.graphql.federation.scheduledEvents.data.repository

import com.github.arhor.aws.graphql.federation.scheduledEvents.data.model.ScheduledEventEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ScheduledEventRepository : CrudRepository<ScheduledEventEntity, UUID>
