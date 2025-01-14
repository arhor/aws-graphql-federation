package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for handling user representations.
 */
public interface UserRepresentationService {

    @NotNull
    Map<UUID, User> findUsersRepresentationsInBatch(@NotNull Set<UUID> userIds);

    /**
     * Creates a new user representation.
     *
     * @param userId         the UUID of the user for whom the representation is to be created
     * @param idempotencyKey the UUID used to ensure idempotency of the creation operation
     */
    void createUserRepresentation(@NotNull UUID userId, @NotNull UUID idempotencyKey);

    /**
     * Deletes an existing user representation.
     *
     * @param userId         the UUID of the user whose representation is to be deleted
     * @param idempotencyKey the UUID used to ensure idempotency of the deletion operation
     */
    void deleteUserRepresentation(@NotNull UUID userId, @NotNull UUID idempotencyKey);

    /**
     * Toggles an ability to create comments for the specified user.
     *
     * @param userId the id of a user
     * @return {@code true} if comments enabled, {@code false} otherwise
     */
    boolean toggleUserComments(@NotNull UUID userId);
}
