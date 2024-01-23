package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@RequiredArgsConstructor
abstract class CommentBatchLoader<T> implements MappedBatchLoader<Long, List<Comment>> {

    private final Executor executor;
    private final Function<Set<Long>, Map<Long, List<Comment>>> function;

    @Override
    public CompletableFuture<Map<Long, List<Comment>>> load(final Set<Long> keys) {
        return CompletableFuture.supplyAsync(() -> function.apply(keys), executor);
    }
}
