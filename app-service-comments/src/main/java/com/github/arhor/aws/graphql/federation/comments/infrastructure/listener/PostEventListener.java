package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.tracing.AttributesKt.IDEMPOTENT_KEY;
import static com.github.arhor.aws.graphql.federation.tracing.AttributesKt.TRACING_ID_KEY;
import static com.github.arhor.aws.graphql.federation.tracing.Utils.withExtendedMDC;

@Trace
@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final PostRepresentationService postRepresentationService;

    @SqsListener("${app-props.aws.sqs.post-created-events:}")
    public void handlePostCreatedEvent(
        @Payload final PostEvent.Created event,
        @Header(TRACING_ID_KEY) final UUID traceId,
        @Header(IDEMPOTENT_KEY) final UUID idempotencyKey
    ) {
        withExtendedMDC(
            traceId,
            () -> postRepresentationService.createPostRepresentation(
                event.getId(),
                idempotencyKey
            )
        );
    }

    @SqsListener("${app-props.aws.sqs.post-deleted-events:}")
    public void handlePostDeletedEvent(
        @Payload final PostEvent.Deleted event,
        @Header(TRACING_ID_KEY) final UUID traceId,
        @Header(IDEMPOTENT_KEY) final UUID idempotencyKey
    ) {
        withExtendedMDC(
            traceId,
            () -> postRepresentationService.deletePostRepresentation(
                event.getId(),
                idempotencyKey
            )
        );
    }
}
