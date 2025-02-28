package com.github.arhor.aws.graphql.federation.votes.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.votes.service.PostRepresentationService
import com.netflix.graphql.dgs.DgsDataLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import java.util.UUID
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class PostRepresentationBatchLoader(
    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    executor: Executor,
    postService: PostRepresentationService,
) : AbstractMappedBatchLoader<UUID, Post>(
    executor = executor,
    loaderFn = postService::findPostsRepresentationsInBatch,
)
