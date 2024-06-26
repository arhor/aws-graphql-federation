package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import java.util.UUID

/**
 * Service interface for handling user representations.
 */
interface UserRepresentationService {

    fun findUsersRepresentationsInBatch(userIds: Set<UUID>): Map<UUID, User>

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

    /**
     * Toggles an ability to operate with posts for the specified user.
     *
     * @param userId the id whose posts should be enabled/disabled
     * @return `true` if the posts enabled, `false` otherwise
     */
    fun toggleUserPosts(userId: UUID): Boolean
}
