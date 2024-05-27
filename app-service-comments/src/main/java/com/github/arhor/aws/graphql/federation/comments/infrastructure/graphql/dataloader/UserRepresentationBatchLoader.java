package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.netflix.graphql.dgs.DgsDataLoader;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@DgsDataLoader(maxBatchSize = 50)
@RequiredArgsConstructor
public class UserRepresentationBatchLoader implements MappedBatchLoader<UUID, User> {

    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private final Executor executor;
    private final UserRepresentationService userService;

    @Override
    public CompletableFuture<Map<UUID, User>> load(final Set<UUID> keys) {
        return keys.isEmpty()
            ? CompletableFuture.completedFuture(Collections.emptyMap())
            : CompletableFuture.supplyAsync(() -> userService.findUsersRepresentationsInBatch(keys), executor);
    }
}
