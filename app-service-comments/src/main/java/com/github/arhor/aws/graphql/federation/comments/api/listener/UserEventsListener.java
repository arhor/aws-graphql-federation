package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@RequiredArgsConstructor
public class UserEventsListener {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CommentService commentService;

    @SqsListener("${app-props.aws.sqs.user-deleted-events}")
    public void handleUserDeletedEvent(final UserEvent.Deleted event) {
        var deletedUserId = event.getId();

        logger.debug("Processing user deleted event with id: {}", deletedUserId);
        commentService.unlinkUserComments(deletedUserId);
    }
}
