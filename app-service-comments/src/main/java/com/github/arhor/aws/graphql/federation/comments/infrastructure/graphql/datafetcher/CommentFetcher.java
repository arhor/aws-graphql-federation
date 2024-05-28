package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.COMMENT;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.CommentChildrenBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.PostCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.UserCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Trace
@DgsComponent
@RequiredArgsConstructor
public class CommentFetcher {

    private final CommentService commentService;

    /* ---------- Queries ---------- */

    @DgsData(parentType = COMMENT.TYPE_NAME, field = COMMENT.Replies)
    public CompletableFuture<List<Comment>> commentReplies(final DgsDataFetchingEnvironment dfe) {
        return loadWith(CommentChildrenBatchLoader.class, dfe, Comment::getId);
    }

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Comments)
    public CompletableFuture<List<Comment>> userComments(final DgsDataFetchingEnvironment dfe) {
        return loadWith(UserCommentsBatchLoader.class, dfe, User::getId);
    }


    @DgsData(parentType = POST.TYPE_NAME, field = POST.Comments)
    public CompletableFuture<List<Comment>> postComments(final DgsDataFetchingEnvironment dfe) {
        return loadWith(PostCommentsBatchLoader.class, dfe, Post::getId);
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public Comment createComment(final @InputArgument CreateCommentInput input) {
        return commentService.createComment(input);
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public Comment updateComment(final @InputArgument UpdateCommentInput input) {
        return commentService.updateComment(input);
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public boolean deleteComment(final @InputArgument DeleteCommentInput input) {
        return commentService.deleteComment(input);
    }

    /* ---------- Internal implementation ---------- */

    /**
     * @param loaderType concrete class of the data loader to use
     * @param dfe        data fetching environment
     * @param extractKey function allowing to extract key from a given entity
     * @param <T>        entity type
     * @param <K>        key type
     * @param <V>        expected result of the data loading
     * @return delayed result of the data loader invocation
     */
    private <T, K, V> CompletableFuture<V> loadWith(
        final Class<? extends MappedBatchLoader<K, V>> loaderType,
        final DgsDataFetchingEnvironment dfe,
        final Function<T, K> extractKey
    ) {
        var loader = dfe.<K, V>getDataLoader(loaderType);
        var entity = dfe.<T>getSource();

        return loader.load(extractKey.apply(entity));
    }
}
