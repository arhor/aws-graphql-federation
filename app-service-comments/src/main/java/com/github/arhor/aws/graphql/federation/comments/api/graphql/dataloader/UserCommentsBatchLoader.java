package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsDataLoader;

import java.util.concurrent.Executor;

@DgsDataLoader(maxBatchSize = 50)
public class UserCommentsBatchLoader extends CommentBatchLoader<User> {

    public UserCommentsBatchLoader(final Executor executor, final CommentService commentService) {
        super(executor, commentService::getCommentsByUserIds);
    }
}
