package com.github.arhor.aws.graphql.federation.votes.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Comment
import com.github.arhor.aws.graphql.federation.votes.service.CommentRepresentationService
import com.netflix.graphql.dgs.DgsDataLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import java.util.UUID
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class CommentRepresentationBatchLoader(
    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    executor: Executor,
    commentRepresentationService: CommentRepresentationService,
) : AbstractMappedBatchLoader<UUID, Comment>(
    executor = executor,
    loaderFn = commentRepresentationService::findCommentsRepresentationsInBatch,
)
