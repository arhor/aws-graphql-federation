package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for handling user representations.
 */
public interface UserRepresentationService {

    Map<UUID, User> findUsersRepresentationsInBatch(Set<UUID> userIds);

    /**
     * Creates a new user representation.
     *
     * @param userId         the UUID of the user for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    void createUserRepresentation(UUID userId, UUID idempotencyKey);

    /**
     * Deletes an existing user representation.
     *
     * @param userId         the UUID of the user whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    void deleteUserRepresentation(UUID userId, UUID idempotencyKey);

    /**
     * Toggles an ability to create comments for the specified user.
     *
     * @param userId the id of a user
     * @return {@code true} if comments enabled, {@code false} otherwise
     */
    boolean toggleUserComments(UUID userId);
}
