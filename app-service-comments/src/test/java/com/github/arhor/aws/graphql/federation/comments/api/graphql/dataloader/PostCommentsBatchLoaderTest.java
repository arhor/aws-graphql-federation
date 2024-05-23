package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PostCommentsBatchLoaderTest {

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final CommentService commentService = mock();

    private final PostCommentsBatchLoader postCommentsBatchLoader = new PostCommentsBatchLoader(
        executor,
        commentService
    );

    @Test
    void should_return_expected_map_when_not_empty_keys_set_provided() {
        // Given
        final var post1Id = UUID.randomUUID();
        final var post2Id = UUID.randomUUID();
        final var postIds = Set.of(post1Id, post2Id);

        final var expectedResult = Map.of(
            post1Id, List.<Comment>of(),
            post2Id, List.<Comment>of()
        );

        given(commentService.getCommentsByPostIds(any()))
            .willReturn(expectedResult);

        // When
        final var result = postCommentsBatchLoader.load(postIds);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(commentService)
                    .should()
                    .getCommentsByPostIds(postIds);

                assertThat(result)
                    .isCompletedWithValue(expectedResult);
            });
    }

    @Test
    void should_return_empty_map_when_empty_keys_set_provided() {
        // Given
        final var postIds = Collections.<UUID>emptySet();

        // When
        final var result = postCommentsBatchLoader.load(postIds);

        // Then
        then(commentService)
            .shouldHaveNoInteractions();

        assertThat(result)
            .isCompletedWithValue(Collections.emptyMap());
    }
}
