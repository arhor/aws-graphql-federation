package com.github.arhor.aws.graphql.federation.votes.service

import java.util.UUID

/**
 * Service interface for handling post representations.
 */
interface PostRepresentationService {

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
