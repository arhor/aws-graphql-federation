package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.common.event.DomainEvent.HEADER_IDEMPOTENCY_ID;

@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final PostService postService;

    @SqsListener("${app-props.aws.sqs.post-created-events:}")
    public void handlePostCreatedEvent(
        @Payload final PostEvent.Created event,
        @Header(HEADER_IDEMPOTENCY_ID) final UUID idempotencyId
    ) {
        postService.createInternalPostRepresentation(event.getId());
    }

    @SqsListener("${app-props.aws.sqs.post-deleted-events:}")
    public void handlePostDeletedEvent(
        @Payload final PostEvent.Deleted event,
        @Header(HEADER_IDEMPOTENCY_ID) final UUID idempotencyId
    ) {
        postService.deleteInternalPostRepresentation(event.getId());
    }
}
