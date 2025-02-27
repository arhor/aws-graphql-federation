package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.model.PostRepresentation
import com.github.arhor.aws.graphql.federation.votes.data.repository.PostRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.service.PostRepresentationService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class PostRepresentationServiceImpl(
    private val postRepository: PostRepresentationRepository,
) : PostRepresentationService {

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
}
