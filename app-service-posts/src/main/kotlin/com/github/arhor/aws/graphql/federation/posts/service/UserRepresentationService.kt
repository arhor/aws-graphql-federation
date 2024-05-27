package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.SwitchUserPostsInput
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
     * Switches the posts for the user based on the provided input.
     *
     * @param input the input object containing the necessary data to switch posts
     * @return `true` if the switch was successful, `false` otherwise
     */
    fun switchPosts(input: SwitchUserPostsInput): Boolean
}
