package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import com.github.arhor.aws.graphql.federation.starter.security.PredefinedAuthority;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Trace
@Service
@RequiredArgsConstructor
public class PostRepresentationServiceImpl implements PostRepresentationService {

    private static final SimpleGrantedAuthority ROLE_ADMIN_AUTH = new SimpleGrantedAuthority(
        PredefinedAuthority.ROLE_ADMIN.name()
    );

    private final PostRepresentationRepository postRepository;
    private final StateGuard stateGuard;

    @NotNull
    @Override
    public Map<UUID, Post> findPostsRepresentationsInBatch(
        @NotNull final Set<UUID> postIds
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
    @Cacheable(cacheNames = "create-post-ops-cache", key = "{#postId, #userId, #idempotencyKey}")
    public void createPostRepresentation(
        @NotNull final UUID postId,
        @NotNull final UUID userId,
        @NotNull final UUID idempotencyKey
    ) {
        final var postRepresentation = PostRepresentation.builder()
            .id(postId)
            .userId(userId)
            .shouldBePersisted(true)
            .build();

        postRepository.save(postRepresentation);

    }

    @Override
    @Cacheable(cacheNames = "delete-post-ops-cache", key = "{#postId, #idempotencyKey}")
    public void deletePostRepresentation(
        @NotNull final UUID postId,
        @NotNull final UUID idempotencyKey
    ) {
        postRepository.deleteById(postId);
    }

    @Override
    public boolean togglePostComments(
        @NotNull final UUID postId,
        @NotNull final CurrentUserDetails actor
    ) {
        final var post = postRepository.findById(postId)
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
        @NotNull final UUID actingUserId,
        @NotNull final Collection<GrantedAuthority> authorities
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
