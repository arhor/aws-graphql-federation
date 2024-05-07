package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsDataLoader;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@DgsDataLoader(maxBatchSize = 50)
@RequiredArgsConstructor
public class PostCommentsBatchLoader implements MappedBatchLoader<Long, List<Comment>> {

    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private final Executor executor;
    private final CommentService commentService;

    @Override
    public CompletableFuture<Map<Long, List<Comment>>> load(final Set<Long> keys) {
        return keys.isEmpty()
            ? CompletableFuture.completedFuture(Collections.emptyMap())
            : CompletableFuture.supplyAsync(() -> commentService.getCommentsByPostIds(keys), executor);
    }
}
