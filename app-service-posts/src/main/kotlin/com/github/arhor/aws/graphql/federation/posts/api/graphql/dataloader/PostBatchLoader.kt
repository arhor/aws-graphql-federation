package com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@Trace
@DgsDataLoader(maxBatchSize = 50)
class PostBatchLoader(
    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private val executor: Executor,
    private val postService: PostService,
) : MappedBatchLoader<UUID, List<Post>> {

    override fun load(keys: Set<UUID>): CompletableFuture<Map<UUID, List<Post>>> =
        if (keys.isEmpty()) {
            CompletableFuture.completedFuture(emptyMap())
        } else {
            CompletableFuture.supplyAsync({ postService.getPostsByUserIds(keys) }, executor)
        }
}
