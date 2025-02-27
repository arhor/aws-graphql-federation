package com.github.arhor.aws.graphql.federation.votes.service

import java.util.UUID

/**
 * Service interface for handling user representations.
 */
interface UserRepresentationService {

    /**
     * Creates a new user representation.
     *
     * @param userId         the UUID of the user for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    fun createUserRepresentation(userId: UUID, idempotencyKey: UUID)

    /**
     * Deletes an existing user representation.
     *
     * @param userId         the UUID of the user whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    fun deleteUserRepresentation(userId: UUID, idempotencyKey: UUID)
}
