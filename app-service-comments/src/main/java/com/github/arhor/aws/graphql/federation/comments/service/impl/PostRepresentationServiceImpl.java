package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.SwitchPostCommentsInput;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.comments.util.Caches;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
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

    @Override
    public Map<UUID, Post> findPostsRepresentationsInBatch(final Set<UUID> postIds) {
        final var result = new HashMap<UUID, Post>(postIds.size());
        final var posts = postRepository.findAllById(postIds);

        for (final var post : posts) {
            result.put(
                post.id(),
                Post.newBuilder()
                    .id(post.id())
                    .commentsOperable(true)
                    .commentsDisabled(post.commentsDisabled())
                    .build()
            );
        }
        postIds.stream().filter(it -> !result.containsKey(it)).forEach((postId) ->
            result.put(
                postId,
                Post.newBuilder()
                    .id(postId)
                    .commentsOperable(false)
                    .build()
            )
        );
        return result;
    }

    @Override
    public void createPostRepresentation(final UUID postId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () ->
            postRepository.save(
                PostRepresentation.builder()
                    .id(postId)
                    .commentsDisabled(false)
                    .shouldBePersisted(true)
                    .build()
            )
        );
    }

    @Override
    public void deletePostRepresentation(final UUID postId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () -> {
            postRepository.deleteById(postId);
            return null;
        });
    }

    @Override
    public boolean switchComments(final SwitchPostCommentsInput input) {
        final var postId = input.getPostId();
        final var shouldBeDisabled = input.getDisabled();

        final var post =
            postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        POST.TYPE_NAME,
                        POST.Id + " = " + postId,
                        Operation.UPDATE
                    )
                );

        if (post.commentsDisabled() != shouldBeDisabled) {
            postRepository.save(
                post.toBuilder()
                    .commentsDisabled(shouldBeDisabled)
                    .build()
            );
            return true;
        } else {
            return false;
        }
    }
}
