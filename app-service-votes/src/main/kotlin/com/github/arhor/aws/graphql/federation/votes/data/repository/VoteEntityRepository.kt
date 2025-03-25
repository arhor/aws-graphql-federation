package com.github.arhor.aws.graphql.federation.votes.data.repository

import com.github.arhor.aws.graphql.federation.votes.data.model.VoteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface VoteEntityRepository : CrudRepository<VoteEntity, UUID> {
    fun findByUserIdAndEntityIdAndEntityType(
        userId: UUID,
        entityId: UUID,
        entityType: VoteEntity.EntityType,
    ): VoteEntity?

    fun findAllByUserIdAndEntityType(
        userId: UUID,
        entityType: VoteEntity.EntityType,
        pageable: Pageable,
    ): Page<VoteEntity>
}
