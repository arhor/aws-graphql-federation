package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
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

class UserCommentsBatchLoaderTest {

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final CommentService commentService = mock();

    private final UserCommentsBatchLoader userCommentsBatchLoader = new UserCommentsBatchLoader(
        executor,
        commentService
    );

    @Test
    void should_return_expected_map_when_not_empty_keys_set_provided() {
        // Given
        final var user1Id = ConstantsKt.getTEST_1_UUID_VAL();
        final var user2Id = ConstantsKt.getTEST_2_UUID_VAL();
        final var userIds = Set.of(user1Id, user2Id);

        final var expectedResult = Map.of(
            user1Id, List.<Comment>of(),
            user2Id, List.<Comment>of()
        );

        given(commentService.getCommentsByUserIds(any()))
            .willReturn(expectedResult);

        // When
        final var result = userCommentsBatchLoader.load(userIds);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(commentService)
                    .should()
                    .getCommentsByUserIds(userIds);

                assertThat(result)
                    .isCompletedWithValue(expectedResult);
            });
    }

    @Test
    void should_return_empty_map_when_empty_keys_set_provided() {
        // Given
        final var userIds = Collections.<UUID>emptySet();

        // When
        final var result = userCommentsBatchLoader.load(userIds);

        // Then
        then(commentService)
            .shouldHaveNoInteractions();

        assertThat(result)
            .isCompletedWithValue(Collections.emptyMap());
    }
}
