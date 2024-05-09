package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PostEventListenerTest {

    private final PostService postService = mock();
    private final PostEventListener postEventListener = new PostEventListener(postService);

    @Test
    void should_call_createInternalPostRepresentation_method_on_post_created_event() {
        // Given
        final var event = new PostEvent.Created(UUID.randomUUID());

        // When
        postEventListener.handlePostCreatedEvent(event);

        // Then
        then(postService)
            .should()
            .createInternalPostRepresentation(event.getId());

        then(postService)
            .shouldHaveNoMoreInteractions();
    }

    @Test
    void should_call_deleteInternalPostRepresentation_method_on_post_deleted_event() {
        // Given
        final var event = new PostEvent.Deleted(UUID.randomUUID());

        // When
        postEventListener.handlePostDeletedEvent(event);

        // Then
        then(postService)
            .should()
            .deleteInternalPostRepresentation(event.getId());

        then(postService)
            .shouldHaveNoMoreInteractions();
    }
}
