package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PostCommentsBatchLoaderTest {

    private final Executor executor = mock();
    private final CommentService commentService = mock();

    private final PostCommentsBatchLoader postCommentsBatchLoader = new PostCommentsBatchLoader(
        executor,
        commentService
    );

    @Test
    void should_return_empty_map_when_empty_keys_set_provided() {
        // Given
        final var postIds = Collections.<UUID>emptySet();

        // When
        final var result = postCommentsBatchLoader.load(postIds);

        // Then
        then(executor)
            .shouldHaveNoInteractions();

        then(commentService)
            .shouldHaveNoInteractions();

        assertThat(result)
            .isCompletedWithValue(Collections.emptyMap());
    }
}
