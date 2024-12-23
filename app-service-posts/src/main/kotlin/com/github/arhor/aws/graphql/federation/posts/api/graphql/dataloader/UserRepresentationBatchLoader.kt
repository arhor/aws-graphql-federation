package com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.netflix.graphql.dgs.DgsDataLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import java.util.UUID
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class UserRepresentationBatchLoader(
    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    executor: Executor,
    userService: UserRepresentationService,
) : AbstractMappedBatchLoader<UUID, User>(
    executor = executor,
    loaderFn = userService::findUsersRepresentationsInBatch,
)
