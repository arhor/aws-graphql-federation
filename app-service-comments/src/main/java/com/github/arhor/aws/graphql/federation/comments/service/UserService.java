package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;

import java.util.Set;

public interface UserService {

    User findInternalUserRepresentation(Long userId);

    void createInternalUserRepresentation(Set<? extends Long> userIds);

    void deleteInternalUserRepresentation(Set<? extends Long> userIds);
}
