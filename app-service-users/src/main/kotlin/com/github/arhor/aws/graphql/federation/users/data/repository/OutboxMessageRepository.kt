package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.model.OutboxMessageEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface OutboxMessageRepository : CrudRepository<OutboxMessageEntity, UUID> {

    @Query(name = "OutboxMessageEntity.findOldestMessagesWithLock")
    fun findOldestMessagesWithLock(type: String, limit: Int): List<OutboxMessageEntity>
}
