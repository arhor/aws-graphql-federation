package com.github.arhor.aws.graphql.federation.votes.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.CreateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.UpdateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Vote
import com.github.arhor.aws.graphql.federation.votes.service.VoteService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import java.util.UUID

@Trace
@DgsComponent
class VoteFetcher(
    private val voteService: VoteService,
) {

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun createVote(@InputArgument input: CreateVoteInput, @AuthenticationPrincipal actor: CurrentUserDetails): Vote =
        voteService.createVote(input, actor)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun updateVote(@InputArgument input: UpdateVoteInput, @AuthenticationPrincipal actor: CurrentUserDetails): Vote =
        voteService.updateVote(input, actor)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun deleteVote(@InputArgument id: UUID, @AuthenticationPrincipal actor: CurrentUserDetails): Boolean =
        voteService.deleteVote(id, actor)
} 
