package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for handling post representations.
 */
public interface PostRepresentationService {

    /**
     * Retrieves post representations for the specified set of post IDs.
     *
     * @param postIds the set of post IDs for which to find representations
     * @return a map where each key is a post ID and the corresponding value is the post representation
     */
    @NotNull
    Map<UUID, Post> findPostsRepresentationsInBatch(@NotNull Set<UUID> postIds);

    /**
     * Creates a new post representation.
     *
     * @param postId         the UUID of the post for whom the representation is to be created
     * @param userId         the UUID of the user for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    void createPostRepresentation(@NotNull UUID postId, @NotNull UUID userId, @NotNull UUID idempotencyKey);

    /**
     * Deletes an existing post representation.
     *
     * @param postId         the UUID of the post whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    void deletePostRepresentation(@NotNull UUID postId, @NotNull UUID idempotencyKey);

    /**
     * Toggles an ability to create comments for the specified post.
     *
     * @param postId the id of a post
     * @param actor  the user trying to toggle comments on a post
     * @return {@code true} if comments enabled, {@code false} otherwise
     */
    boolean togglePostComments(@NotNull UUID postId, @NotNull CurrentUserDetails actor);
}
