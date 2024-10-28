package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.common.constants.AttributesKt.ATTR_IDEMPOTENCY_KEY;
import static com.github.arhor.aws.graphql.federation.common.constants.AttributesKt.ATTR_TRACE_ID;
import static com.github.arhor.aws.graphql.federation.starter.tracing.Utils.withExtendedMDC;

@Trace
@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final PostRepresentationService postRepresentationService;

    @SqsListener("${app-props.events.source.sync-comments-on-post-created-event}")
    public void syncCommentsOnPostCreatedEvent(
        @Payload final PostEvent.Created event,
        @Header(ATTR_TRACE_ID) final UUID traceId,
        @Header(ATTR_IDEMPOTENCY_KEY) final UUID idempotencyKey
    ) {
        withExtendedMDC(traceId, () -> {
            postRepresentationService.createPostRepresentation(
                event.getId(),
                event.getUserId(),
                idempotencyKey
            );
        });
    }

    @SqsListener("${app-props.events.source.sync-comments-on-post-deleted-event}")
    public void syncCommentsOnPostDeletedEvent(
        @Payload final PostEvent.Deleted event,
        @Header(ATTR_TRACE_ID) final UUID traceId,
        @Header(ATTR_IDEMPOTENCY_KEY) final UUID idempotencyKey
    ) {
        withExtendedMDC(traceId, () -> {
            postRepresentationService.deletePostRepresentation(
                event.getId(),
                idempotencyKey
            );
        });
    }
}
