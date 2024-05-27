package com.github.arhor.aws.graphql.federation.posts.infrastructure.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.netflix.graphql.dgs.DgsDataLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import java.util.UUID
import java.util.concurrent.Executor

@Trace
@DgsDataLoader(maxBatchSize = 50)
class PostBatchLoader(
    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    executor: Executor,
    postService: PostService,
) : AbstractMappedBatchLoader<UUID, List<Post>>(
    executor = executor,
    loaderFn = postService::getPostsByUserIds
)
