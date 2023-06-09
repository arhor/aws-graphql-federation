package com.github.arhor.dgs.topics.graphql.loader

import com.github.arhor.dgs.topics.generated.graphql.types.Post
import com.github.arhor.dgs.topics.service.PostService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function

sealed class PostBatchLoader(
    private val executor: Executor,
    private val loader: Function<Set<Long>, Map<Long, List<Post>>>,
) : MappedBatchLoader<Long, List<Post>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<Post>>> {
        return CompletableFuture.supplyAsync({ loader.apply(keys) }, executor)
    }

    @DgsDataLoader(name = "userPosts", maxBatchSize = 250)
    class ForUser(asyncExecutor: Executor, postService: PostService) :
        PostBatchLoader(executor = asyncExecutor, loader = postService::getPostsByUserIds)

    @DgsDataLoader(name = "topicPosts", maxBatchSize = 250)
    class ForTopic(asyncExecutor: Executor, postService: PostService) :
        PostBatchLoader(executor = asyncExecutor, loader = postService::getPostsByTopicIds)
}
