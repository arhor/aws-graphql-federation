package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for handling user representations.
 */
public interface UserRepresentationService {

    @Nonnull
    Map<UUID, User> findUsersRepresentationsInBatch(@Nonnull Set<UUID> userIds);

    /**
     * Creates a new user representation.
     *
     * @param userId         the UUID of the user for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    void createUserRepresentation(@Nonnull UUID userId, @Nonnull UUID idempotencyKey);

    /**
     * Deletes an existing user representation.
     *
     * @param userId         the UUID of the user whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    void deleteUserRepresentation(@Nonnull UUID userId, @Nonnull UUID idempotencyKey);

    /**
     * Toggles an ability to create comments for the specified user.
     *
     * @param userId the id of a user
     * @return {@code true} if comments enabled, {@code false} otherwise
     */
    boolean toggleUserComments(@Nonnull UUID userId);
}
