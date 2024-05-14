package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.PostService;
import com.github.arhor.aws.graphql.federation.comments.service.UserService;
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

    private final UserService userService;
    private final PostService postService;

    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    public User resolveUser(final Map<String, ?> values) {
        return userService.findInternalUserRepresentation(getUuid(values, USER.Id));
    }

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    public Post resolvePost(final Map<String, ?> values) {
        return postService.findInternalPostRepresentation(getUuid(values, POST.Id));
    }
}
