package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

@SpringBootTest(
    classes = {
        DgsAutoConfiguration.class,
        DgsExtendedScalarsAutoConfiguration.class,
        FederatedEntityFetcher.class,
    }
)
class FederatedEntityFetcherTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @Test
    void should_create_new_user_representation_for_the_given_id() {
        // Given
        final var userId = 1L;

        // When
        var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query ($representations: [_Any!]!) {
                    _entities(representations: $representations) {
                        ... on User {
                            id
                        }
                    }
                }""".stripIndent(),
            "$.data._entities[0]",
            Map.of("representations", List.of(Map.of("__typename", USER.TYPE_NAME, USER.Id, userId))),
            User.class
        );

        // Then
        assertThat(result)
            .returns(userId, from(User::getId));
    }

    @Test
    void should_create_new_post_representation_for_the_given_id() {
        // Given
        final var postId = 1L;

        // When
        var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query ($representations: [_Any!]!) {
                    _entities(representations: $representations) {
                        ... on Post {
                            id
                        }
                    }
                }""".stripIndent(),
            "$.data._entities[0]",
            Map.of("representations", List.of(Map.of("__typename", POST.TYPE_NAME, POST.Id, postId))),
            Post.class
        );

        // Then
        assertThat(result)
            .returns(postId, from(Post::getId));
    }
}
