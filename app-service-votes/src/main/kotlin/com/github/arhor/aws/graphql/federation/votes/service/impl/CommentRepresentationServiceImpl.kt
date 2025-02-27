package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.model.CommentRepresentation
import com.github.arhor.aws.graphql.federation.votes.data.repository.CommentRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.service.CommentRepresentationService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class CommentRepresentationServiceImpl(
    private val commentRepository: CommentRepresentationRepository,
) : CommentRepresentationService {

    @Cacheable(cacheNames = ["create-comment-representation-requests-cache"])
    override fun createCommentRepresentation(commentId: UUID, postId: UUID, userId: UUID, idempotencyKey: UUID) {
        commentRepository.save(
            CommentRepresentation(
                id = commentId,
                userId = userId,
                postId = postId,
                shouldBePersisted = true,
            )
        )
    }

    @Cacheable(cacheNames = ["delete-comment-representation-requests-cache"])
    override fun deleteCommentRepresentation(commentId: UUID, idempotencyKey: UUID) {
        commentRepository.deleteById(commentId)
    }
}
