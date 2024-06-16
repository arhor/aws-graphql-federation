package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.comments.util.Caches;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import com.github.arhor.aws.graphql.federation.starter.security.PredefinedAuthority;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.comments.util.CacheManagerUtils.getCache;

@Trace
@Service
@RequiredArgsConstructor
public class PostRepresentationServiceImpl implements PostRepresentationService {

    private static final SimpleGrantedAuthority ROLE_ADMIN_AUTH = new SimpleGrantedAuthority(PredefinedAuthority.ROLE_ADMIN.name());

    private final CacheManager cacheManager;
    private final PostRepresentationRepository postRepository;
    private final StateGuard stateGuard;

    private Cache cache;

    @PostConstruct
    public void initialize() {
        cache = getCache(cacheManager, Caches.IDEMPOTENT_ID_SET);
    }

    @Nonnull
    @Override
    public Map<UUID, Post> findPostsRepresentationsInBatch(
        @Nonnull final Set<UUID> postIds
    ) {
        final var result = new HashMap<UUID, Post>(postIds.size());
        final var posts = postRepository.findAllById(postIds);

        for (final var post : posts) {
            result.put(
                post.id(),
                Post.newBuilder()
                    .id(post.id())
                    .commentsDisabled(post.commentsDisabled())
                    .build()
            );
        }
        for (final var postId : postIds) {
            if (result.containsKey(postId)) {
                continue;
            }
            result.put(postId, Post.newBuilder().id(postId).build());
        }
        return result;
    }

    @Override
    public void createPostRepresentation(
        @Nonnull final UUID postId,
        @Nonnull final UUID userId,
        @Nonnull final UUID idempotencyKey
    ) {
        cache.get(idempotencyKey, () ->
            postRepository.save(
                PostRepresentation.builder()
                    .id(postId)
                    .userId(userId)
                    .shouldBePersisted(true)
                    .build()
            )
        );
    }

    @Override
    public void deletePostRepresentation(
        @Nonnull final UUID postId,
        @Nonnull final UUID idempotencyKey
    ) {
        cache.get(idempotencyKey, () -> {
            postRepository.deleteById(postId);
            return null;
        });
    }

    @Override
    public boolean togglePostComments(
        @Nonnull final UUID postId,
        @Nonnull final CurrentUserDetails actor
    ) {
        final var post =
            postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        POST.TYPE_NAME,
                        POST.Id + " = " + postId,
                        Operation.UPDATE
                    )
                );

        ensureOperationAllowed(
            post.userId(),
            actor.getId(),
            actor.getAuthorities()
        );

        final var updatedPost = postRepository.save(post.toggleComments());

        return !updatedPost.commentsDisabled();
    }

    private void ensureOperationAllowed(
        @Nullable final UUID targetUserId,
        @Nonnull final UUID actingUserId,
        @Nonnull final Collection<GrantedAuthority> authorities
    ) {
        final var operation = Operation.UPDATE;

        if (!actingUserId.equals(targetUserId) && !authorities.contains(ROLE_ADMIN_AUTH)) {
            throw new EntityOperationRestrictedException(
                POST.TYPE_NAME,
                "Not enough permissions to operate post comments",
                operation
            );
        }
        stateGuard.ensureCommentsEnabled(POST.TYPE_NAME, operation, StateGuard.Type.USER, actingUserId);
    }
}
