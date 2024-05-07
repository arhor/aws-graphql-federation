package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.BDDMockito.then;

@SpringJUnitConfig(PostEventsListener.class)
class PostEventsListenerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private PostEventsListener postEventsListener;

    @Test
    void should_call_deletePostComments_method_on_post_deleted_event() {
        // Given
        final var postId = 1L;
        final var event = new PostEvent.Deleted(postId);

        // When
        postEventsListener.handlePostDeletedEvent(event);

        // Then
        then(commentService)
            .should()
            .deletePostComments(postId);
    }
}
