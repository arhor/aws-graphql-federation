package com.github.arhor.aws.graphql.federation.votes.service

import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.CreateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.UpdateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Vote
import java.util.UUID

interface VoteService {
    fun createVote(input: CreateVoteInput): Vote
    fun updateVote(input: UpdateVoteInput): Vote
    fun deleteVote(id: UUID): Boolean
}
