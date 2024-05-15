package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;

import java.util.UUID;

public interface UserRepresentationService {

    User findUserRepresentation(UUID userId);

    void createUserRepresentation(UUID userId, UUID idempotencyKey);

    void deleteUserRepresentation(UUID userId, UUID idempotencyKey);
}
