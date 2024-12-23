package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.netflix.graphql.dgs.DgsDataLoader;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;
import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@DgsDataLoader(maxBatchSize = 50)
public class PostRepresentationBatchLoader extends AbstractMappedBatchLoader<UUID, Post> {

    public PostRepresentationBatchLoader(
        @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME) Executor executor,
        final PostRepresentationService postService
    ) {
        super(executor, postService::findPostsRepresentationsInBatch);
    }
}
