package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxMessageEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface OutboxMessageRepository : CrudRepository<OutboxMessageEntity, Long> {

    @Query(name = "OutboxMessageEntity.dequeueOldest")
    fun dequeueOldest(messageType: String, messagesNum: Int): List<OutboxMessageEntity>
}
