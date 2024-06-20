package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.comments.util.Caches;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.comments.util.CacheManagerUtils.getCache;

@Trace
@Service
@RequiredArgsConstructor
public class UserRepresentationServiceImpl implements UserRepresentationService {

    private final CacheManager cacheManager;
    private final UserRepresentationRepository userRepository;

    private Cache cache;

    @PostConstruct
    public void initialize() {
        cache = getCache(cacheManager, Caches.IDEMPOTENT_ID_SET);
    }

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
    public void createUserRepresentation(
        @NotNull final UUID userId,
        @NotNull final UUID idempotencyKey
    ) {
        cache.get(idempotencyKey, () ->
            userRepository.save(
                UserRepresentation.builder()
                    .id(userId)
                    .shouldBePersisted(true)
                    .build()
            )
        );
    }

    @Override
    public void deleteUserRepresentation(
        @NotNull final UUID userId,
        @NotNull final UUID idempotencyKey
    ) {
        cache.get(idempotencyKey, () -> {
            userRepository.deleteById(userId);
            return null;
        });
    }

    @Override
    public boolean toggleUserComments(
        @NotNull final UUID userId
    ) {
        return userRepository.findById(userId)
            .map(user -> userRepository.save(user.toggleComments()))
            .map(user -> !user.commentsDisabled())
            .orElseThrow(() -> new EntityNotFoundException(
                    USER.TYPE_NAME,
                    USER.Id + " = " + userId,
                    Operation.UPDATE
                )
            );
    }
}
