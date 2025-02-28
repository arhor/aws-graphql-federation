package com.github.arhor.aws.graphql.federation.votes.service

import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Post
import java.util.UUID

/**
 * Service interface for handling post representations.
 */
interface PostRepresentationService {

    /**
     * Retrieves post representations for the specified set of post IDs.
     *
     * @param postIds the set of post IDs for which to find representations
     * @return a map where each key is a post ID and the corresponding value is the post representation
     */
    fun findPostsRepresentationsInBatch(postIds: Set<UUID>): Map<UUID, Post>

    /**
     * Creates a new post representation.
     *
     * @param postId         the UUID of the post for whom the representation is to be created
     * @param userId         the UUID of the user for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    fun createPostRepresentation(postId: UUID, userId: UUID, idempotencyKey: UUID)

    /**
     * Deletes an existing post representation.
     *
     * @param postId         the UUID of the post whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    fun deletePostRepresentation(postId: UUID, idempotencyKey: UUID)
}
