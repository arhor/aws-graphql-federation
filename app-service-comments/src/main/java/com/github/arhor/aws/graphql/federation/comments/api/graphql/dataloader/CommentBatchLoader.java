package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public abstract class CommentBatchLoader<T> implements MappedBatchLoader<Long, List<Comment>> {

    private final Executor executor;
    private final Function<Set<Long>, Map<Long, List<Comment>>> function;

    public CommentBatchLoader(
        final Executor executor,
        final Function<Set<Long>, Map<Long, List<Comment>>> function
    ) {
        this.executor = executor;
        this.function = function;
    }

    @Override
    public CompletableFuture<Map<Long, List<Comment>>> load(final Set<Long> keys) {
        return CompletableFuture.supplyAsync(() -> function.apply(keys), executor);
    }

    @DgsDataLoader(maxBatchSize = 50)
    public static class ForUser extends CommentBatchLoader<User> {

        public ForUser(Executor executor, CommentService commentService) {
            super(executor, commentService::getCommentsByUserIds);
        }
    }

    @DgsDataLoader(maxBatchSize = 50)
    public static class ForPost extends CommentBatchLoader<Post> {

        public ForPost(Executor executor, CommentService commentService) {
            super(executor, commentService::getCommentsByPostIds);
        }
    }
}
