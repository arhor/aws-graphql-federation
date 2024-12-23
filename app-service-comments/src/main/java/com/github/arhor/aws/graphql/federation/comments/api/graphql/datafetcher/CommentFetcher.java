package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.CommentRepliesBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.PostCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.PostCommentsNumberBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.UserCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.COMMENT;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Trace
@DgsComponent
@RequiredArgsConstructor
public class CommentFetcher {

    private final CommentService commentService;

    /* ---------- Queries ---------- */

    @DgsQuery
    public Comment comment(@InputArgument final UUID id) {
        return commentService.getCommentById(id);
    }

    @DgsData(parentType = COMMENT.TYPE_NAME, field = COMMENT.Replies)
    public CompletableFuture<List<Comment>> commentReplies(final DgsDataFetchingEnvironment dfe) {
        return loadWith(CommentRepliesBatchLoader.class, dfe, Comment::getId);
    }

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Comments)
    public CompletableFuture<List<Comment>> userComments(final DgsDataFetchingEnvironment dfe) {
        return loadWith(UserCommentsBatchLoader.class, dfe, User::getId);
    }


    @DgsData(parentType = POST.TYPE_NAME, field = POST.Comments)
    public CompletableFuture<List<Comment>> postComments(final DgsDataFetchingEnvironment dfe) {
        return loadWith(PostCommentsBatchLoader.class, dfe, Post::getId);
    }

    @DgsData(parentType = POST.TYPE_NAME, field = POST.CommentsNumber)
    public CompletableFuture<Integer> postCommentsNumber(final DgsDataFetchingEnvironment dfe) {
        return loadWith(PostCommentsNumberBatchLoader.class, dfe, Post::getId);
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public Comment createComment(
        final @InputArgument CreateCommentInput input,
        final @AuthenticationPrincipal CurrentUserDetails actor
    ) {
        return commentService.createComment(input, actor);
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public Comment updateComment(
        final @InputArgument UpdateCommentInput input,
        final @AuthenticationPrincipal CurrentUserDetails actor
    ) {
        return commentService.updateComment(input, actor);
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public boolean deleteComment(
        final @InputArgument UUID id,
        final @AuthenticationPrincipal CurrentUserDetails actor
    ) {
        return commentService.deleteComment(id, actor);
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
    @NotNull
    private <T, K, V> CompletableFuture<V> loadWith(
        @NotNull final Class<? extends MappedBatchLoader<K, V>> loaderType,
        @NotNull final DgsDataFetchingEnvironment dfe,
        @NotNull final Function<T, K> extractKey
    ) {
        return Optional.ofNullable(dfe.<T>getSource())
            .map(extractKey)
            .map(key -> dfe.<K, V>getDataLoader(loaderType).load(key))
            .orElseGet(() -> CompletableFuture.completedFuture(null));
    }
}
