package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.comments.util.Caches;
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
public class UserRepresentationServiceImpl implements UserRepresentationService {

    private final CacheManager cacheManager;
    private final UserRepresentationRepository userRepresentationRepository;

    private Cache cache;

    @PostConstruct
    public void initialize() {
        cache = getCache(cacheManager, Caches.IDEMPOTENT_ID_SET);
    }

    @Override
    public User findUserRepresentation(final UUID userId) {
        return userRepresentationRepository.findById(userId)
            .map((user) ->
                User.newBuilder()
                    .id(user.id())
                    .commentsOperable(true)
                    .commentsDisabled(user.commentsDisabled())
                    .build()
            )
            .orElseGet(() ->
                User.newBuilder()
                    .id(userId)
                    .commentsOperable(false)
                    .build()
            );
    }

    @Override
    public void createUserRepresentation(final UUID userId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () ->
            userRepresentationRepository.save(
                UserRepresentation.builder()
                    .id(userId)
                    .commentsDisabled(false)
                    .shouldBePersisted(true)
                    .build()
            )
        );
    }

    @Override
    public void deleteUserRepresentation(final UUID userId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () -> {
            userRepresentationRepository.deleteById(userId);
            return null;
        });
    }
}
