package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
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

class UserRepresentationBatchLoaderTest {

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final UserRepresentationService userService = mock();

    private final UserRepresentationBatchLoader userRepresentationBatchLoader = new UserRepresentationBatchLoader(
        executor,
        userService
    );

    @Test
    void should_return_expected_map_when_not_empty_keys_set_provided() {
        // Given
        final var user1Id = UUID.randomUUID();
        final var user2Id = UUID.randomUUID();
        final var userIds = Set.of(user1Id, user2Id);

        final var expectedResult = Map.of(
            user1Id, User.newBuilder().id(user1Id).build(),
            user2Id, User.newBuilder().id(user2Id).build()
        );

        given(userService.findUsersRepresentationsInBatch(any()))
            .willReturn(expectedResult);

        // When
        final var result = userRepresentationBatchLoader.load(userIds);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(userService)
                    .should()
                    .findUsersRepresentationsInBatch(userIds);

                assertThat(result)
                    .isCompletedWithValue(expectedResult);
            });
    }

    @Test
    void should_return_empty_map_when_empty_keys_set_provided() {
        // Given
        final var postIds = Collections.<UUID>emptySet();

        // When
        final var result = userRepresentationBatchLoader.load(postIds);

        // Then
        then(userService)
            .shouldHaveNoInteractions();

        assertThat(result)
            .isCompletedWithValue(Collections.emptyMap());
    }
}
