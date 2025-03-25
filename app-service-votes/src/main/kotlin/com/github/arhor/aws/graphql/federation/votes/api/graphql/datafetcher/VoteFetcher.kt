package com.github.arhor.aws.graphql.federation.votes.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.votes.service.VoteService
import com.netflix.graphql.dgs.DgsComponent

@DgsComponent
class VoteFetcher(
    private val voteService: VoteService,
) {

    /* ---------- Mutations ---------- */
} 
