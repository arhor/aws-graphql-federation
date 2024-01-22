package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsEntityFetcher;

import java.util.Map;

import static com.github.arhor.aws.graphql.federation.common.MapExtKt.getLong;

@DgsComponent
class FederatedEntityFetchers {

    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    public User resolveUser(final Map<String, ?> values) {
        return new User(
            /* id = */ getLong(values, USER.Id),
            /* comments = */ null
        );
    }

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    public Post resolvePost(final Map<String, ?> values) {
        return new Post(
            /* id = */ getLong(values, POST.Id),
            /* comments = */ null
        );
    }
}
