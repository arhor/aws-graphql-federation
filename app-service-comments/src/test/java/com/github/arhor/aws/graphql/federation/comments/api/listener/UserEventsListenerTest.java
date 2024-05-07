package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Set;

import static org.mockito.BDDMockito.then;

@SpringJUnitConfig(UserEventsListener.class)
class UserEventsListenerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private UserEventsListener userEventsListener;

    @Test
    void should_call_createInternalUserRepresentation_method_on_user_created_event() {
        // Given
        final var userIds = Set.of(1L);
        final var event = new UserEvent.Created(userIds);

        // When
        userEventsListener.handleUserCreatedEvent(event);

        // Then
        then(userService)
            .should()
            .createInternalUserRepresentation(userIds);
    }

    @Test
    void should_call_deleteInternalUserRepresentation_method_on_user_deleted_event() {
        // Given
        final var userIds = Set.of(1L);
        final var event = new UserEvent.Deleted(userIds);

        // When
        userEventsListener.handleUserDeletedEvent(event);

        // Then
        then(userService)
            .should()
            .deleteInternalUserRepresentation(userIds);
    }
}
