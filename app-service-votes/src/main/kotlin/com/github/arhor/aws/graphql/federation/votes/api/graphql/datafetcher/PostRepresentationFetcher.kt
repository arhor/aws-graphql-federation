package com.github.arhor.aws.graphql.federation.votes.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.getUuid
import com.github.arhor.aws.graphql.federation.votes.api.graphql.dataloader.PostRepresentationBatchLoader
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Post
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsEntityFetcher
import java.util.UUID
import java.util.concurrent.CompletableFuture

@DgsComponent
class PostRepresentationFetcher {

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    fun resolvePost(values: Map<String, Any>, dfe: DgsDataFetchingEnvironment): CompletableFuture<Post> {
        val postId = values.getUuid(POST.Id)
        val loader = dfe.getDataLoader<UUID, Post>(PostRepresentationBatchLoader::class.java)

        return loader.load(postId)
    }
}
