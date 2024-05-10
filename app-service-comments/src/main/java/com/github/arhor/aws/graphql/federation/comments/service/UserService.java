package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;

import java.util.UUID;

public interface UserService {

    User findInternalUserRepresentation(UUID userId);

    void createInternalUserRepresentation(UUID userId, UUID idempotencyId);

    void deleteInternalUserRepresentation(UUID userId, UUID idempotencyId);
}
