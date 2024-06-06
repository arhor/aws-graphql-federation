package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.Test;

import java.util.Collections;
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

class PostRepresentationBatchLoaderTest {

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final PostRepresentationService postService = mock();

    private final PostRepresentationBatchLoader postRepresentationBatchLoader = new PostRepresentationBatchLoader(
        executor,
        postService
    );

    @Test
    void should_return_expected_map_when_not_empty_keys_set_provided() {
        // Given
        final var post1Id = ConstantsKt.getTEST_1_UUID_VAL();
        final var post2Id = ConstantsKt.getTEST_2_UUID_VAL();
        final var postIds = Set.of(post1Id, post2Id);

        final var expectedResult = Map.of(
            post1Id, Post.newBuilder().id(post1Id).build(),
            post2Id, Post.newBuilder().id(post2Id).build()
        );

        given(postService.findPostsRepresentationsInBatch(any()))
            .willReturn(expectedResult);

        // When
        final var result = postRepresentationBatchLoader.load(postIds);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(postService)
                    .should()
                    .findPostsRepresentationsInBatch(postIds);

                assertThat(result)
                    .isCompletedWithValue(expectedResult);
            });
    }

    @Test
    void should_return_empty_map_when_empty_keys_set_provided() {
        // Given
        final var postIds = Collections.<UUID>emptySet();

        // When
        final var result = postRepresentationBatchLoader.load(postIds);

        // Then
        then(postService)
            .shouldHaveNoInteractions();

        assertThat(result)
            .isCompletedWithValue(Collections.emptyMap());
    }
}
