package com.github.arhor.aws.graphql.federation.votes.service

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.CreateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.UpdateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Vote
import java.util.UUID

interface VoteService {
    fun createVote(input: CreateVoteInput, actor: CurrentUserDetails): Vote
    fun updateVote(input: UpdateVoteInput, actor: CurrentUserDetails): Vote
    fun deleteVote(id: UUID, actor: CurrentUserDetails): Boolean
}
