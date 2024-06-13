package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments.Feature;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.comments.util.Caches;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
public class PostRepresentationServiceImpl implements PostRepresentationService {

    private final CacheManager cacheManager;
    private final PostRepresentationRepository postRepository;

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
                    .commentsDisabled(post.features().check(Feature.COMMENTS_DISABLED))
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
        @Nonnull final UUID idempotencyKey
    ) {
        cache.get(idempotencyKey, () ->
            postRepository.save(
                PostRepresentation.builder()
                    .id(postId)
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
        @Nonnull final UUID postId
    ) {
        final var post =
            postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        POST.TYPE_NAME,
                        POST.Id + " = " + postId,
                        Operation.UPDATE
                    )
                );

        final var updatedPost =
            postRepository.save(
                post.toBuilder()
                    .features(post.features().toggle(Feature.COMMENTS_DISABLED))
                    .build()
            );

        return !updatedPost.features().check(Feature.COMMENTS_DISABLED);
    }
}
