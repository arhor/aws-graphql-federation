package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventsListener {

    private final PostService postService;

    @SqsListener("${app-props.aws.sqs.post-created-events}")
    public void handlePostCreatedEvent(final PostEvent.Created event) {
        postService.createInternalPostRepresentation(event.getId());
    }

    @SqsListener("${app-props.aws.sqs.post-deleted-events}")
    public void handlePostDeletedEvent(final PostEvent.Deleted event) {
        postService.deleteInternalPostRepresentation(event.getId());
    }
}
