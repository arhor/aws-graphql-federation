package com.github.arhor.aws.graphql.federation.users.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.service.AuthService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@Trace
@DgsDataLoader(maxBatchSize = 50)
class AuthBatchLoader(
    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private val executor: Executor,
    private val authService: AuthService,
) : MappedBatchLoader<UUID, List<String>> {

    override fun load(keys: Set<UUID>): CompletableFuture<Map<UUID, List<String>>> =
        if (keys.isEmpty()) {
            CompletableFuture.completedFuture(emptyMap())
        } else {
            CompletableFuture.supplyAsync({ authService.getAuthoritiesByUserIds(keys) }, executor)
        }
}
