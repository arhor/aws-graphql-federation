package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.CommentBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@DgsComponent
public class CommentFetcher {

    private final CommentService commentService;

    public CommentFetcher(final CommentService commentService) {
        this.commentService = commentService;
    }

    /* Queries */

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Comments)
    public CompletableFuture<List<Comment>> userComments(final DgsDataFetchingEnvironment dfe) {
        return loadCommentsUsing(CommentBatchLoader.ForUser.class, dfe, User::getId);
    }


    @DgsData(parentType = POST.TYPE_NAME, field = POST.Comments)
    public CompletableFuture<List<Comment>> postComments(final DgsDataFetchingEnvironment dfe) {
        return loadCommentsUsing(CommentBatchLoader.ForPost.class, dfe, Post::getId);
    }

    /* Mutations */

    @DgsMutation
    public CreateCommentResult createComment(@InputArgument CreateCommentInput input) {
        return commentService.createComment(input);
    }

    @DgsMutation
    public UpdateCommentResult updateComment(@InputArgument UpdateCommentInput input) {
        return commentService.updateComment(input);
    }

    /* Internal implementation */

    private <T extends CommentBatchLoader<D>, D> CompletableFuture<List<Comment>> loadCommentsUsing(
        final Class<T> clazz,
        final DgsDataFetchingEnvironment dfe,
        final Function<D, Long> id
    ) {
        var loader = dfe.<Long, List<Comment>>getDataLoader(clazz);
        var source = dfe.<D>getSource();

        return loader.load(id.apply(source));
    }
}
