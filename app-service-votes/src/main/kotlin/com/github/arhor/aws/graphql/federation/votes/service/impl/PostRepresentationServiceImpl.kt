package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.model.PostRepresentation
import com.github.arhor.aws.graphql.federation.votes.data.repository.PostRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.votes.service.PostRepresentationService
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class PostRepresentationServiceImpl(
    private val postRepository: PostRepresentationRepository,
) : PostRepresentationService {

    override fun findPostsRepresentationsInBatch(postIds: Set<UUID>): Map<UUID, Post> {
        if (postIds.isEmpty()) {
            return emptyMap()
        }
        val result =
            postRepository
                .findAllById(postIds)
                .associateByTo(
                    destination = HashMap(postIds.size),
                    keySelector = { it.id },
                    valueTransform = { Post(id = it.id) },
                )

        (postIds - result.keys).takeIf(Set<UUID>::isNotEmpty)?.let {
            for (postId in it) {
                result[postId] = Post(id = postId)
            }
            logger.warn("Stubbed posts without representation in the DB: {}", it)
        }
        return result
    }

    @Cacheable(cacheNames = ["create-post-representation-requests-cache"])
    override fun createPostRepresentation(postId: UUID, userId: UUID, idempotencyKey: UUID) {
        postRepository.save(
            PostRepresentation(
                id = postId,
                userId = userId,
                shouldBePersisted = true,
            )
        )
    }

    @Cacheable(cacheNames = ["delete-post-representation-requests-cache"])
    override fun deletePostRepresentation(postId: UUID, idempotencyKey: UUID) {
        postRepository.deleteById(postId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
