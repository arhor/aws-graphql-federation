package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
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
    private CommentService commentService;

    @Autowired
    private UserEventsListener userEventsListener;

    @Test
    void should_call_unlinkUserComments_method_on_user_deleted_event() {
        // given
        final var userIds = Set.of(1L);
        final var event = new UserEvent.Deleted(userIds);

        // when
        userEventsListener.handleUserDeletedEvent(event);

        // then
        then(commentService)
            .should()
            .unlinkUsersComments(userIds);
    }
}
