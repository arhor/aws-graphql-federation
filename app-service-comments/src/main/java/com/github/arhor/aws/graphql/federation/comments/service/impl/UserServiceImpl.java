package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserService;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Trace
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findInternalUserRepresentation(final UUID userId) {
        return userRepository.findById(userId)
            .map(this::mapEntityToUser)
            .orElseThrow(() -> new EntityNotFoundException(
                USER.TYPE_NAME,
                USER.Id + " = " + userId,
                Operation.LOOKUP
            ));
    }

    @Override
    public void createInternalUserRepresentation(final Set<? extends UUID> userIds) {
        final var entities = userIds.stream()
            .map(UserEntity::new)
            .toList();

        userRepository.saveAll(entities);
    }

    @Override
    public void deleteInternalUserRepresentation(final Set<? extends UUID> userIds) {
        userRepository.deleteAllById(userIds);
    }

    private User mapEntityToUser(final UserEntity entity) {
        return User.newBuilder()
            .id(entity.id())
            .build();
    }
}
