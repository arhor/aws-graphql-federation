package com.github.arhor.aws.graphql.federation.users.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.users.service.AuthService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class AuthBatchLoader(
    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private val executor: Executor,
    private val authService: AuthService,
) : MappedBatchLoader<Long, List<String>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<String>>> {
        return CompletableFuture.supplyAsync({ authService.getAuthoritiesByUserIds(keys) }, executor)
    }
}
