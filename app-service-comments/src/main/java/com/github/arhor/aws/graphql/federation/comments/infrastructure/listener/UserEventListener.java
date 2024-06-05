package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.starter.tracing.AttributesKt.IDEMPOTENT_KEY;
import static com.github.arhor.aws.graphql.federation.starter.tracing.AttributesKt.TRACING_ID_KEY;
import static com.github.arhor.aws.graphql.federation.starter.tracing.Utils.withExtendedMDC;

@Trace
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepresentationService userRepresentationService;

    @SqsListener("${app-props.aws.sqs.user-created-events:}")
    public void handleUserCreatedEvent(
        @Payload final UserEvent.Created event,
        @Header(TRACING_ID_KEY) final UUID traceId,
        @Header(IDEMPOTENT_KEY) final UUID idempotencyKey
    ) {
        withExtendedMDC(
            traceId,
            () -> userRepresentationService.createUserRepresentation(
                event.getId(),
                idempotencyKey
            )
        );
    }

    @SqsListener("${app-props.aws.sqs.user-deleted-events:}")
    public void handleUserDeletedEvent(
        @Payload final UserEvent.Deleted event,
        @Header(TRACING_ID_KEY) final UUID traceId,
        @Header(IDEMPOTENT_KEY) final UUID idempotencyKey
    ) {
        withExtendedMDC(
            traceId,
            () -> userRepresentationService.deleteUserRepresentation(
                event.getId(),
                idempotencyKey
            )
        );
    }
}
