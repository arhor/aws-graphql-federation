package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.common.constants.AttributesKt.ATTR_TRACE_ID;
import static com.github.arhor.aws.graphql.federation.starter.tracing.Utils.withExtendedMDC;

@Trace
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepresentationService userRepresentationService;

    @SqsListener("${app-props.events.source.sync-comments-on-user-created-event}")
    public void syncCommentsOnUserCreatedEvent(
        @Payload final UserEvent.Created event,
        @Header(ATTR_TRACE_ID) final UUID traceId
    ) {
        withExtendedMDC(traceId, () -> {
            userRepresentationService.createUserRepresentation(
                event.getId(),
                traceId
            );
        });
    }

    @SqsListener("${app-props.events.source.sync-comments-on-user-deleted-event}")
    public void syncCommentsOnUserDeletedEvent(
        @Payload final UserEvent.Deleted event,
        @Header(ATTR_TRACE_ID) final UUID traceId
    ) {
        withExtendedMDC(traceId, () -> {
            userRepresentationService.deleteUserRepresentation(
                event.getId(),
                traceId
            );
        });
    }
}
