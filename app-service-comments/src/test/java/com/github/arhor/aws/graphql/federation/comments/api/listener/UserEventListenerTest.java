package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class UserEventListenerTest {

    private final UserService userService = mock();
    private final UserEventListener userEventListener = new UserEventListener(userService);

    @Test
    void should_call_createInternalUserRepresentation_method_on_user_created_event() {
        // Given
        final var event = new UserEvent.Created(UUID.randomUUID());

        // When
        userEventListener.handleUserCreatedEvent(event);

        // Then
        then(userService)
            .should()
            .createInternalUserRepresentation(event.getIds());
    }

    @Test
    void should_call_deleteInternalUserRepresentation_method_on_user_deleted_event() {
        // Given
        final var event = new UserEvent.Deleted(UUID.randomUUID());

        // When
        userEventListener.handleUserDeletedEvent(event);

        // Then
        then(userService)
            .should()
            .deleteInternalUserRepresentation(event.getIds());
    }
}
