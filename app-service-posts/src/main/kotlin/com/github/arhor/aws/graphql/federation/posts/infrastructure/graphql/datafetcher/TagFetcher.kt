package com.github.arhor.aws.graphql.federation.posts.infrastructure.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.posts.infrastructure.graphql.dataloader.TagBatchLoader
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Trace
@DgsComponent
class TagFetcher {

    /* ---------- Queries ---------- */

    @DgsData(parentType = POST.TYPE_NAME, field = POST.Tags)
    fun postTags(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<String>> {
        val source = dfe.getSource<Post>() ?: return CompletableFuture.completedFuture(null)
        val loader = dfe.getDataLoader<UUID, List<String>>(TagBatchLoader::class.java)

        return loader.load(source.id)
    }
}
