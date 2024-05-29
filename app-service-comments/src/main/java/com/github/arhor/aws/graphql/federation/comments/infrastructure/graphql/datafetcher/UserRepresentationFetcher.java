package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.SwitchUserCommentsInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.UserRepresentationBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.github.arhor.aws.graphql.federation.common.MapExtKt.getUuid;

@Trace
@DgsComponent
@RequiredArgsConstructor
public class UserRepresentationFetcher {

    private final UserRepresentationService userService;

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    public CompletableFuture<User> resolveUser(final Map<String, ?> values, final DgsDataFetchingEnvironment dfe) {
        final var userId = getUuid(values, USER.Id);
        final var loader = dfe.<UUID, User>getDataLoader(UserRepresentationBatchLoader.class);

        return loader.load(userId);
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("hasRole('ADMIN')")
    public boolean switchUserComments(final @InputArgument SwitchUserCommentsInput input) {
        return userService.switchUserComments(input);
    }
}
