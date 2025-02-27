package com.github.arhor.aws.graphql.federation.votes.service

import java.util.UUID

/**
 * Service interface for handling comment representations.
 */
interface CommentRepresentationService {

    /**
     * Creates a new comment representation.
     *
     * @param commentId      the UUID of the comment for whom the representation is to be created
     * @param postId         the UUID of the post for whom the representation is to be created
     * @param userId         the UUID of the user for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    fun createCommentRepresentation(commentId: UUID, postId: UUID, userId: UUID, idempotencyKey: UUID)

    /**
     * Deletes an existing comment representation.
     *
     * @param commentId      the UUID of the post whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    fun deleteCommentRepresentation(commentId: UUID, idempotencyKey: UUID)
}
