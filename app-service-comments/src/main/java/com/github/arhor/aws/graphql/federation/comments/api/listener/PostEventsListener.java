package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class PostEventsListener {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CommentService commentService;

    public PostEventsListener(final CommentService commentService) {
        this.commentService = commentService;
    }

    @SqsListener("${app-props.aws.sqs.post-deleted-events}")
    public void handlePostDeletedEvent(final PostEvent.Deleted event) {
        var deletedPostId = event.getId();

        logger.debug("Processing post deleted event with id: {}", deletedPostId);
        commentService.deletePostComments(deletedPostId);
    }
}
