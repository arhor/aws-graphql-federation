package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Trace
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @SqsListener("${app-props.aws.sqs.user-created-events:}")
    public void handleUserCreatedEvent(final UserEvent.Created event) {
        userService.createInternalUserRepresentation(event.getIds());
    }

    @SqsListener("${app-props.aws.sqs.user-deleted-events:}")
    public void handleUserDeletedEvent(final UserEvent.Deleted event) {
        userService.deleteInternalUserRepresentation(event.getIds());
    }
}
