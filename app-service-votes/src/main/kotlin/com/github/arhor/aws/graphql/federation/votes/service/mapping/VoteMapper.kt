package com.github.arhor.aws.graphql.federation.votes.service.mapping

import com.github.arhor.aws.graphql.federation.votes.data.model.VoteEntity
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.CreateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Vote
import java.util.UUID

interface VoteMapper {
    fun mapToEntity(input: CreateVoteInput, userId: UUID): VoteEntity
    fun mapToVote(entity: VoteEntity): Vote
} 
