package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Trace
@Component
@RequiredArgsConstructor
public class UserEventsListener {

    private final CommentService commentService;

    @SqsListener("${app-props.aws.sqs.user-deleted-events}")
    public void handleUserDeletedEvent(final UserEvent.Deleted event) {
        commentService.unlinkUsersComments(event.getIds());
    }
}
