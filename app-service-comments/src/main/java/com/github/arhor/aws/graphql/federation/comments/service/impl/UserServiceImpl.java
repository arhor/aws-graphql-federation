package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserService;
import com.github.arhor.aws.graphql.federation.comments.util.Caches;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.comments.util.CacheManagerUtils.getCache;

@Trace
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CacheManager cacheManager;
    private final UserRepository userRepository;

    private Cache cache;

    @PostConstruct
    public void initialize() {
        cache = getCache(cacheManager, Caches.IDEMPOTENT_ID_SET);
    }

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
    public void createInternalUserRepresentation(final UUID userId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () ->
            userRepository.save(
                new UserEntity(userId)
            )
        );
    }

    @Override
    public void deleteInternalUserRepresentation(final UUID userId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () -> {
            userRepository.deleteById(userId);
            return null;
        });
    }

    private User mapEntityToUser(final UserEntity entity) {
        return User.newBuilder()
            .id(entity.id())
            .build();
    }
}
