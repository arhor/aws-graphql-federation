package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsDataLoader;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@DgsDataLoader(maxBatchSize = 50)
public class CommentRepliesBatchLoader extends AbstractMappedBatchLoader<UUID, List<Comment>> {

    public CommentRepliesBatchLoader(
        final @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME) Executor executor,
        final CommentService commentService
    ) {
        super(executor, commentService::getCommentsReplies);
    }
}
