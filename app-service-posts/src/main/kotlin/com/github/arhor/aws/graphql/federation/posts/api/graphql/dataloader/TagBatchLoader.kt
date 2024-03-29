package com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.service.TagService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import org.springframework.beans.factory.annotation.Qualifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class TagBatchLoader(
    @Qualifier("dgsAsyncTaskExecutor")
    private val executor: Executor,
    private val tagService: TagService,
) : MappedBatchLoader<Long, List<String>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<String>>> =
        if (keys.isEmpty()) {
            CompletableFuture.completedFuture(emptyMap())
        } else {
            CompletableFuture.supplyAsync({ tagService.getTagsByPostIds(keys) }, executor)
        }
}
