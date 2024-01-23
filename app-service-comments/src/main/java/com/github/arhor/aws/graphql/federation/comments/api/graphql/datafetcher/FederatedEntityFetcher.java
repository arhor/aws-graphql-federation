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
public class FederatedEntityFetcher {

    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    public User resolveUser(final Map<String, ?> values) {
        return User.newBuilder()
            .id(getLong(values, USER.Id))
            .build();
    }

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    public Post resolvePost(final Map<String, ?> values) {
        return Post.newBuilder()
            .id(getLong(values, POST.Id))
            .build();
    }
}
