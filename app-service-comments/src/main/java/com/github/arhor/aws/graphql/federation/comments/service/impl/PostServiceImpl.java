package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostService;
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
public class PostServiceImpl implements PostService {

    private final CacheManager cacheManager;
    private final PostRepository postRepository;

    private Cache cache;

    @PostConstruct
    public void initialize() {
        cache = getCache(cacheManager, Caches.IDEMPOTENT_ID_SET);
    }

    @Override
    public Post findInternalPostRepresentation(final UUID postId) {
        return postRepository.findById(postId)
            .map(this::mapEntityToPost)
            .orElseThrow(() -> new EntityNotFoundException(
                POST.TYPE_NAME,
                POST.Id + " = " + postId,
                Operation.LOOKUP
            ));
    }

    @Override
    public void createInternalPostRepresentation(final UUID postId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () ->
            postRepository.save(
                new PostEntity(postId)
            )
        );
    }

    @Override
    public void deleteInternalPostRepresentation(final UUID postId, final UUID idempotencyKey) {
        cache.get(idempotencyKey, () -> {
            postRepository.deleteById(postId);
            return null;
        });
    }

    private Post mapEntityToPost(final PostEntity entity) {
        return Post.newBuilder()
            .id(entity.id())
            .build();
    }
}
