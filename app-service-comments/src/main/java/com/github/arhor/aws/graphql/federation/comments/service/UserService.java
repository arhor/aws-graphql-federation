package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;

import java.util.Set;
import java.util.UUID;

public interface UserService {

    User findInternalUserRepresentation(UUID userId);

    void createInternalUserRepresentation(Set<? extends UUID> userIds);

    void deleteInternalUserRepresentation(Set<? extends UUID> userIds);
}
