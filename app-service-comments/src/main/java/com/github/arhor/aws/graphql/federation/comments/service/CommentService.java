package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for handling comments.
 */
public interface CommentService {

    /**
     * Retrieves a comment by its ID.
     *
     * @param id the UUID of the comment to retrieve
     * @return the comment with the specified ID
     */
    @Nonnull
    Comment getCommentById(@Nonnull UUID id);

    /**
     * Retrieves comments for the specified user IDs.
     *
     * @param userIds the collection of user IDs to retrieve comments for
     * @return a map where the key is the user ID and the value is a list of comments associated with that user
     */
    @Nonnull
    Map<UUID, List<Comment>> getCommentsByUserIds(@Nonnull Collection<UUID> userIds);

    /**
     * Retrieves comments for the specified post IDs.
     *
     * @param postIds the collection of post IDs to retrieve comments for
     * @return a map where the key is the post ID and the value is a list of comments associated with that post
     */
    @Nonnull
    Map<UUID, List<Comment>> getCommentsByPostIds(@Nonnull Collection<UUID> postIds);

    /**
     * Retrieves replies for the specified comments IDs.
     *
     * @param commentIds the collection of comment IDs to retrieve reply comments for
     * @return a map where the key is the comment ID and the value is a list of reply comments associated with that comment
     */
    @Nonnull
    Map<UUID, List<Comment>> getCommentsReplies(@Nonnull Collection<UUID> commentIds);

    /**
     * Retrieves number of comments for the specified post IDs.
     *
     * @param postIds the collection of post IDs to retrieve number of comments for
     * @return a map where the key is the post ID and the value is a number of comments associated with that post
     */
    @Nonnull
    Map<UUID, Integer> getCommentsNumberByPostIds(@Nonnull Collection<UUID> postIds);

    /**
     * Creates a new comment.
     *
     * @param input the input object containing the necessary data to create the comment
     * @param actor the user creating comment
     * @return the created comment
     */
    @Nonnull
    Comment createComment(@Nonnull CreateCommentInput input, @Nonnull CurrentUserDetails actor);

    /**
     * Updates an existing comment.
     *
     * @param input the input object containing the necessary data to update the comment
     * @param actor the user updating comment
     * @return the updated comment
     */
    @Nonnull
    Comment updateComment(@Nonnull UpdateCommentInput input, @Nonnull CurrentUserDetails actor);

    /**
     * Deletes a comment.
     *
     * @param id    the id of a comment to be deleted
     * @param actor the user deleting comment
     * @return {@code true} if the comment was successfully deleted, {@code false} otherwise
     */
    boolean deleteComment(@Nonnull UUID id, @Nonnull CurrentUserDetails actor);
}
