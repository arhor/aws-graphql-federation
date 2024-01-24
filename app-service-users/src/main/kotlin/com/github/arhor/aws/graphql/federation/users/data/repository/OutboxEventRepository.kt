package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import org.springframework.data.repository.CrudRepository

interface OutboxEventRepository : CrudRepository<OutboxEventEntity, Long> {
}
