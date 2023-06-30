package com.github.arhor.dgs.posts.api.graphql.dataloader

import com.github.arhor.dgs.posts.generated.graphql.types.Post
import com.github.arhor.dgs.posts.service.PostService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class PostBatchLoader(
    private val asyncExecutor: Executor,
    private val postService: PostService,
) : MappedBatchLoader<Long, List<Post>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<Post>>> {
        return CompletableFuture.supplyAsync({ postService.getPostsByUserIds(keys) }, asyncExecutor)
    }
}
