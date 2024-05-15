package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.tracing.AttributesKt.TRACING_ID_KEY;
import static com.github.arhor.aws.graphql.federation.tracing.Utils.withExtendedMDC;

@Trace
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepresentationService userRepresentationService;

    @SqsListener("${app-props.aws.sqs.user-created-events}")
    public void handleUserCreatedEvent(
        @Payload final UserEvent.Created event,
        @Header(TRACING_ID_KEY) final UUID traceId
    ) {
        withExtendedMDC(
            traceId,
            () -> userRepresentationService.createUserRepresentation(
                event.getId(),
                traceId
            )
        );
    }

    @SqsListener("${app-props.aws.sqs.user-deleted-events}")
    public void handleUserDeletedEvent(
        @Payload final UserEvent.Deleted event,
        @Header(TRACING_ID_KEY) final UUID traceId
    ) {
        withExtendedMDC(
            traceId,
            () -> userRepresentationService.deleteUserRepresentation(
                event.getId(),
                traceId
            )
        );
    }
}
