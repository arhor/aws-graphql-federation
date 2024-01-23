package com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsDataLoader;

import java.util.concurrent.Executor;

@DgsDataLoader(maxBatchSize = 50)
public class PostCommentsBatchLoader extends CommentBatchLoader<Post> {

    public PostCommentsBatchLoader(final Executor executor, final CommentService commentService) {
        super(executor, commentService::getCommentsByPostIds);
    }
}
