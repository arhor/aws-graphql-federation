package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface OutboxEventRepository : CrudRepository<OutboxEventEntity, Long> {

    @Query(name = "OutboxEventEntity.dequeueOldest")
    fun dequeueOldest(eventsNum: Int): List<OutboxEventEntity>
}
