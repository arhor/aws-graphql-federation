package com.github.arhor.aws.graphql.federation.posts.infrastructure.graphql.dataloader

import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

abstract class AbstractMappedBatchLoader<K, V>(
    private val executor: Executor,
    private val loaderFn: (Set<K>) -> Map<K, V>,
) : MappedBatchLoader<K, V> {

    @Override
    override fun load(keys: Set<K>): CompletableFuture<Map<K, V>> =
        if (keys.isEmpty()) {
            CompletableFuture.completedFuture(emptyMap())
        } else {
            CompletableFuture.supplyAsync({ loaderFn.invoke(keys) }, executor)
        }
}
