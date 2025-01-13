package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Trace
@Service
@RequiredArgsConstructor
public class UserRepresentationServiceImpl implements UserRepresentationService {

    private final UserRepresentationRepository userRepository;

    @NotNull
    @Override
    public Map<UUID, User> findUsersRepresentationsInBatch(
        @NotNull final Set<UUID> userIds
    ) {
        final var result = new HashMap<UUID, User>(userIds.size());
        final var users = userRepository.findAllById(userIds);

        for (final var user : users) {
            result.put(
                user.id(),
                User.newBuilder()
                    .id(user.id())
                    .commentsDisabled(user.commentsDisabled())
                    .build()
            );
        }
        for (final var userId : userIds) {
            if (result.containsKey(userId)) {
                continue;
            }
            result.put(userId, User.newBuilder().id(userId).build());
        }
        return result;
    }

    @Override
    @Cacheable
    public void createUserRepresentation(@NotNull final UUID userId) {
        final var userRepresentation = UserRepresentation.builder()
            .id(userId)
            .shouldBePersisted(true)
            .build();

        userRepository.save(userRepresentation);
    }

    @Override
    @Cacheable
    public void deleteUserRepresentation(@NotNull final UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean toggleUserComments(
        @NotNull final UUID userId
    ) {
        final var user = userRepository.findById(userId)
            .map(UserRepresentation::toggleComments)
            .map(userRepository::save)
            .orElseThrow(() -> new EntityNotFoundException(USER.TYPE_NAME, USER.Id + " = " + userId, Operation.UPDATE));

        return !user.commentsDisabled();
    }
}
