package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsDataLoader;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@DgsDataLoader(maxBatchSize = 50)
public class PostCommentsBatchLoader extends CommentBatchLoader<Post> {

    public PostCommentsBatchLoader(
        @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
        final Executor executor,
        final CommentService commentService
    ) {
        super(executor, commentService::getCommentsByPostIds);
    }
}
