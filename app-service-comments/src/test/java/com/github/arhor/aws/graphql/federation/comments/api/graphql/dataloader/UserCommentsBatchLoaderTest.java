package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class UserCommentsBatchLoaderTest {

    private final Executor executor = mock();
    private final CommentService commentService = mock();

    private final UserCommentsBatchLoader userCommentsBatchLoader = new UserCommentsBatchLoader(
        executor,
        commentService
    );

    @Test
    void should_return_empty_map_when_empty_keys_set_provided() {
        // given
        final var userIds = Collections.<Long>emptySet();

        // When
        final var result = userCommentsBatchLoader.load(userIds);

        // then
        then(executor)
            .shouldHaveNoInteractions();

        then(commentService)
            .shouldHaveNoInteractions();

        assertThat(result)
            .isCompletedWithValue(Collections.emptyMap());
    }
}
