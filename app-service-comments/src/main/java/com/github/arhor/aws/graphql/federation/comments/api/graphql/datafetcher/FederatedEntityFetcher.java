package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.github.arhor.aws.graphql.federation.common.MapExtKt.getUuid;

@Trace
@DgsComponent
@RequiredArgsConstructor
public class FederatedEntityFetcher {

    private final UserRepresentationService userRepresentationService;
    private final PostRepresentationService postRepresentationService;

    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    public User resolveUser(final Map<String, ?> values) {
        return userRepresentationService.findUserRepresentation(
            getUuid(
                values,
                USER.Id
            )
        );
    }

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    public Post resolvePost(final Map<String, ?> values) {
        return postRepresentationService.findPostRepresentation(
            getUuid(
                values,
                POST.Id
            )
        );
    }
}
