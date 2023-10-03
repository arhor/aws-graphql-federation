package com.github.arhor.dgs.posts.api.graphql.dataloader

import com.github.arhor.dgs.posts.service.TagService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class TagBatchLoader(
    private val asyncExecutor: Executor,
    private val tagService: TagService,
) : MappedBatchLoader<Long, List<String>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<String>>> {
        return CompletableFuture.supplyAsync({ tagService.getTagsByPostIds(keys) }, asyncExecutor)
    }
}
