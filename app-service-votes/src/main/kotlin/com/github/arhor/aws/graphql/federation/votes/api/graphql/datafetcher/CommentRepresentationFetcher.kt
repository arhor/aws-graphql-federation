package com.github.arhor.aws.graphql.federation.votes.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.getUuid
import com.github.arhor.aws.graphql.federation.votes.api.graphql.dataloader.CommentRepresentationBatchLoader
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants.COMMENT
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Comment
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsEntityFetcher
import java.util.UUID
import java.util.concurrent.CompletableFuture

@DgsComponent
class CommentRepresentationFetcher {

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = COMMENT.TYPE_NAME)
    fun resolveCommemnt(values: Map<String, Any>, dfe: DgsDataFetchingEnvironment): CompletableFuture<Comment> {
        val commentId = values.getUuid(COMMENT.Id)
        val loader = dfe.getDataLoader<UUID, Comment>(CommentRepresentationBatchLoader::class.java)

        return loader.load(commentId)
    }
}
