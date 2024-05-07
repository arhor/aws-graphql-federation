package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.PostService;
import com.github.arhor.aws.graphql.federation.comments.service.UserService;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
    classes = {
        DgsAutoConfiguration.class,
        DgsExtendedScalarsAutoConfiguration.class,
        FederatedEntityFetcher.class,
    }
)
class FederatedEntityFetcherTest {

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @Test
    void should_create_new_user_representation_for_the_given_id() {
        // Given
        final var userId = 1L;
        final var expectedUser = User.newBuilder().id(userId).build();

        when(userService.findInternalUserRepresentation(any()))
            .thenReturn(expectedUser);

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
            .isNotNull()
            .isEqualTo(expectedUser);
    }

    @Test
    void should_create_new_post_representation_for_the_given_id() {
        // Given
        final var postId = 1L;
        final var expectedPost = Post.newBuilder().id(postId).build();

        when(postService.findInternalPostRepresentation(any()))
            .thenReturn(expectedPost);

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
            .isNotNull()
            .isEqualTo(expectedPost);
    }
}
