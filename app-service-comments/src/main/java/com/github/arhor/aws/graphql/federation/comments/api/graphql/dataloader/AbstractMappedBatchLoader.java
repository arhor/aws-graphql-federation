package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

@RequiredArgsConstructor
public abstract class AbstractMappedBatchLoader<K, V> implements MappedBatchLoader<K, V> {

    private final Executor executor;
    private final Function<Set<K>, Map<K, V>> loaderFn;

    @Override
    public CompletableFuture<Map<K, V>> load(
        final Set<K> keys
    ) {
        return keys.isEmpty()
            ? CompletableFuture.completedFuture(emptyMap())
            : CompletableFuture.supplyAsync(() -> loaderFn.apply(keys), executor);
    }
}
