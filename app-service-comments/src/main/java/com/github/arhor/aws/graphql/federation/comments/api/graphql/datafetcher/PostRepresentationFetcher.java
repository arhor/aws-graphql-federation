package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.PostRepresentationBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.github.arhor.aws.graphql.federation.common.MapExtKt.getUuid;

@Trace
@DgsComponent
@RequiredArgsConstructor
public class PostRepresentationFetcher {

    private final PostRepresentationService postService;

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    public CompletableFuture<Post> resolvePost(final Map<String, ?> values, final DgsDataFetchingEnvironment dfe) {
        final var postId = getUuid(values, POST.Id);
        final var loader = dfe.<UUID, Post>getDataLoader(PostRepresentationBatchLoader.class);

        return loader.load(postId);
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public boolean togglePostComments(
        final @InputArgument UUID postId,
        final @AuthenticationPrincipal CurrentUserDetails actor
    ) {
        return postService.togglePostComments(postId, actor);
    }
}
