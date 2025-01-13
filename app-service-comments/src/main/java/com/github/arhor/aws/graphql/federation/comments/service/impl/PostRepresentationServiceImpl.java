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
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.starter.security.UtilsKt.isAdmin;

@Slf4j
@Trace
@Service
@RequiredArgsConstructor
public class PostRepresentationServiceImpl implements PostRepresentationService {

    private final PostRepresentationRepository postRepository;
    private final StateGuard stateGuard;

    @NotNull
    @Override
    public Map<UUID, Post> findPostsRepresentationsInBatch(
        @NotNull final Set<UUID> postIds
    ) {
        if (postIds.isEmpty()) {
            return Map.of();
        }
        final var result = new HashMap<UUID, Post>(postIds.size());
        final var posts = postRepository.findAllById(postIds);

        for (final var post : posts) {
            result.put(
                post.id(),
                mapRepresentatioToDto(post)
            );
        }
        for (final var postId : postIds) {
            if (result.containsKey(postId)) {
                continue;
            }
            result.put(postId, createDummyDto(postId));
        }
        return result;
    }

    @Override
    @Cacheable
    public void createPostRepresentation(
        @NotNull final UUID postId,
        @NotNull final UUID userId
    ) {
        postRepository.save(
            createNewPostRepresentation(
                postId,
                userId
            )
        );
    }

    @Override
    @Cacheable
    public void deletePostRepresentation(
        @NotNull final UUID postId
    ) {
        postRepository.deleteById(postId);
    }

    @Override
    public boolean togglePostComments(
        @NotNull final UUID postId,
        @NotNull final CurrentUserDetails actor
    ) {
        final var post =
            postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        POST.TYPE_NAME,
                        POST.Id + " = " + postId,
                        Operation.UPDATE
                    )
                );

        ensureOperationAllowed(post.userId(), actor);

        final var updatedPost = postRepository.save(post.toggleComments());

        return !updatedPost.commentsDisabled();
    }

    @NotNull
    private Post mapRepresentatioToDto(
        @NotNull final PostRepresentation post
    ) {
        return Post.newBuilder()
            .id(post.id())
            .commentsDisabled(post.commentsDisabled())
            .build();
    }

    @NotNull
    private Post createDummyDto(
        @NotNull final UUID postId
    ) {
        return Post.newBuilder()
            .id(postId)
            .build();
    }

    @NotNull
    private PostRepresentation createNewPostRepresentation(
        @NotNull final UUID postId,
        @NotNull final UUID userId
    ) {
        return PostRepresentation.builder()
            .id(postId)
            .userId(userId)
            .shouldBePersisted(true)
            .build();
    }

    private void ensureOperationAllowed(
        @Nullable final UUID targetUserId,
        @NotNull final CurrentUserDetails actor
    ) {
        final var operation = Operation.UPDATE;
        final var actorId = actor.getId();

        if (!actorId.equals(targetUserId) && !isAdmin(actor)) {
            throw new EntityOperationRestrictedException(
                POST.TYPE_NAME,
                "Not enough permissions to operate post comments",
                operation
            );
        }
        stateGuard.ensureCommentsEnabled(POST.TYPE_NAME, operation, StateGuard.Type.USER, actorId);
    }
}
