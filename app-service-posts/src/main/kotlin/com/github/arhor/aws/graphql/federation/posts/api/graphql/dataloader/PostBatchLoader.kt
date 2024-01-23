package com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import org.springframework.beans.factory.annotation.Qualifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class PostBatchLoader(
    @Qualifier("dgsAsyncTaskExecutor")
    private val executor: Executor,
    private val postService: PostService,
) : MappedBatchLoader<Long, List<Post>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<Post>>> {
        return CompletableFuture.supplyAsync({ postService.getPostsByUserIds(keys) }, executor)
    }
}
