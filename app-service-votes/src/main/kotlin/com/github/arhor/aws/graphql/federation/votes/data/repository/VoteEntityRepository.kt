package com.github.arhor.aws.graphql.federation.votes.data.repository

import com.github.arhor.aws.graphql.federation.votes.data.model.VoteEntity
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteEntityType
import org.springframework.data.repository.CrudRepository
import java.util.UUID
import java.util.stream.Stream

interface VoteEntityRepository : CrudRepository<VoteEntity, UUID> {
    fun findAllByEntityIdAndEntityType(
        entityId: UUID,
        entityType: VoteEntityType,
    ): Stream<VoteEntity>

    fun existsByUserIdAndEntityIdAndEntityType(
        userId: UUID,
        entityId: UUID,
        entityType: VoteEntityType,
    ): Boolean
}
