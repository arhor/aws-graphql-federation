package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.common.event.DomainEvent.HEADER_IDEMPOTENCY_ID;

@Trace
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @SqsListener("${app-props.aws.sqs.user-created-events:}")
    public void handleUserCreatedEvent(
        @Payload final UserEvent.Created event,
        @Header(HEADER_IDEMPOTENCY_ID) final UUID idempotencyId
    ) {
        userService.createInternalUserRepresentation(event.getId());
    }

    @SqsListener("${app-props.aws.sqs.user-deleted-events:}")
    public void handleUserDeletedEvent(
        @Payload final UserEvent.Deleted event,
        @Header(HEADER_IDEMPOTENCY_ID) final UUID idempotencyId
    ) {
        userService.deleteInternalUserRepresentation(event.getId());
    }
}
