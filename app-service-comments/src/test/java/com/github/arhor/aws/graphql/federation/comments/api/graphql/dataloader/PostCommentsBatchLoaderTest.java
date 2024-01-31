package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;

class PostCommentsBatchLoaderTest {

    private final Executor executor = mock();
    private final CommentService commentService = mock();

    private PostCommentsBatchLoader postCommentsBatchLoader = new PostCommentsBatchLoader(
        executor,
        commentService
    );

    @Test
    void should_pass() {
        // given

        // when

        // then

    }
}
