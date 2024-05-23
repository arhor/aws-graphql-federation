package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.SwitchUserCommentsInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
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
public class UserRepresentationServiceImpl implements UserRepresentationService {

    private final CacheManager cacheManager;
    private final UserRepresentationRepository userRepository;

    private Cache cache;

    @PostConstruct
    public void initialize() {
        cache = getCache(cacheManager, Caches.IDEMPOTENT_ID_SET);
    }

    @Override
    public User findUserRepresentation(final UUID userId) {
        return userRepository.findById(userId)
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
            userRepository.save(
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
            userRepository.deleteById(userId);
            return null;
        });
    }

    @Override
    public boolean switchComments(final SwitchUserCommentsInput input) {
        final var userId = input.getUserId();
        final var shouldBeDisabled = input.getDisabled();

        final var user =
            userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        USER.TYPE_NAME,
                        USER.Id + " = " + userId,
                        Operation.UPDATE
                    )
                );

        if (user.commentsDisabled() != shouldBeDisabled) {
            userRepository.save(
                user.toBuilder()
                    .commentsDisabled(shouldBeDisabled)
                    .build()
            );
            return true;
        } else {
            return false;
        }
    }
}
