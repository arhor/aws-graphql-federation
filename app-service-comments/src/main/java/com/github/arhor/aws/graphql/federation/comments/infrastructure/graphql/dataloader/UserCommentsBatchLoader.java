package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsDataLoader;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.util.Collections.emptyMap;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@DgsDataLoader(maxBatchSize = 50)
@RequiredArgsConstructor
public class UserCommentsBatchLoader implements MappedBatchLoader<UUID, List<Comment>> {

    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private final Executor executor;
    private final CommentService commentService;

    @Override
    public CompletableFuture<Map<UUID, List<Comment>>> load(final Set<UUID> keys) {
        return keys.isEmpty()
            ? CompletableFuture.completedFuture(emptyMap())
            : CompletableFuture.supplyAsync(() -> commentService.getCommentsByUserIds(keys), executor);
    }
}
