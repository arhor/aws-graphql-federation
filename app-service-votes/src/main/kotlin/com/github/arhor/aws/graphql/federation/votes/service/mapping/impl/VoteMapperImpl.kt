package com.github.arhor.aws.graphql.federation.votes.service.mapping.impl

import com.github.arhor.aws.graphql.federation.votes.data.model.VoteEntity
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.CreateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Vote
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteEntityType
import com.github.arhor.aws.graphql.federation.votes.service.mapping.VoteMapper
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class VoteMapperImpl : VoteMapper {

    override fun mapToEntity(input: CreateVoteInput, userId: UUID): VoteEntity = VoteEntity(
        userId = userId,
        entityId = input.entityId,
        entityType = VoteEntity.EntityType.valueOf(input.entityType.name),
        value = input.value
    )

    override fun mapToVote(entity: VoteEntity): Vote = Vote(
        id = entity.id!!,
        userId = entity.userId,
        entityId = entity.entityId,
        entityType = VoteEntityType.valueOf(entity.entityType.name),
        value = entity.value
    )
} 
