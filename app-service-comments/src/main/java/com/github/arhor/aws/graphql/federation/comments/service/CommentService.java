package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for handling comments.
 */
public interface CommentService {

    Map<UUID, List<Comment>> getCommentsChildren(Collection<UUID> commentIds);

    /**
     * Retrieves comments for the specified user IDs.
     *
     * @param userIds the collection of user IDs to retrieve comments for
     * @return a map where the key is the user ID and the value is a list of comments associated with that user
     */
    Map<UUID, List<Comment>> getCommentsByUserIds(Collection<UUID> userIds);

    /**
     * Retrieves comments for the specified post IDs.
     *
     * @param postIds the collection of post IDs to retrieve comments for
     * @return a map where the key is the post ID and the value is a list of comments associated with that post
     */
    Map<UUID, List<Comment>> getCommentsByPostIds(Collection<UUID> postIds);

    /**
     * Creates a new comment.
     *
     * @param input the input object containing the necessary data to create the comment
     * @return the created comment
     */
    Comment createComment(CreateCommentInput input);

    /**
     * Updates an existing comment.
     *
     * @param input the input object containing the necessary data to update the comment
     * @return the updated comment
     */
    Comment updateComment(UpdateCommentInput input);

    /**
     * Deletes a comment.
     *
     * @param input the input object containing the necessary data to delete the comment
     * @return {@code true} if the comment was successfully deleted, {@code false} otherwise
     */
    boolean deleteComment(DeleteCommentInput input);
}
