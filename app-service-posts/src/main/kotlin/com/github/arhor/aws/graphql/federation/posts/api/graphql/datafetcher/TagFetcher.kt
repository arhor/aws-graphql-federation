package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.TagBatchLoader
import com.github.arhor.dgs.posts.generated.graphql.DgsConstants
import com.github.arhor.dgs.posts.generated.graphql.types.Post
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import java.util.concurrent.CompletableFuture

@DgsComponent
class TagFetcher {

    /* Queries */

    @DgsData(parentType = DgsConstants.POST.TYPE_NAME)
    fun tags(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<String>> {
        val loader = dfe.getDataLoader<Long, List<String>>(TagBatchLoader::class.java)
        val source = dfe.getSource<Post>()

        return loader.load(source.id)
    }
}
