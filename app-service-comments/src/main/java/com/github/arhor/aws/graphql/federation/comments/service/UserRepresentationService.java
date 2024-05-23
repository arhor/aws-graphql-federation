package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.SwitchUserCommentsInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;

import java.util.UUID;

/**
 * Service interface for handling user representations.
 */
public interface UserRepresentationService {

    /**
     * Finds the user representation by the specified user ID.
     *
     * @param userId the UUID of the user to find the representation for
     * @return the user representation
     */
    User findUserRepresentation(UUID userId);

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
     * Switches the comments for the user based on the provided input.
     *
     * @param input the input object containing the necessary data to switch comments
     * @return {@code true} if the switch was successful, {@code false} otherwise
     */
    boolean switchComments(SwitchUserCommentsInput input);
}
