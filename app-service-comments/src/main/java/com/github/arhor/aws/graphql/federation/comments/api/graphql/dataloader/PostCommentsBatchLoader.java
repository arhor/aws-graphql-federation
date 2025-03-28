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
public class PostCommentsBatchLoader extends AbstractMappedBatchLoader<UUID, List<Comment>> {

    public PostCommentsBatchLoader(
        @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME) final Executor executor,
        final CommentService commentService
    ) {
        super(executor, commentService::getCommentsByPostIds);
    }
}
