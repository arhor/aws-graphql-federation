package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for handling post representations.
 */
public interface PostRepresentationService {

    Map<UUID, Post> findPostsRepresentationsInBatch(Set<UUID> postIds);

    /**
     * Creates a new post representation.
     *
     * @param postId         the UUID of the post for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    void createPostRepresentation(UUID postId, UUID idempotencyKey);

    /**
     * Deletes an existing post representation.
     *
     * @param postId         the UUID of the post whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    void deletePostRepresentation(UUID postId, UUID idempotencyKey);

    /**
     * Toggles an ability to create comments for the specified post.
     *
     * @param postId the id of a post
     * @return {@code true} if comments enabled, {@code false} otherwise
     */
    boolean togglePostComments(UUID postId);
}
