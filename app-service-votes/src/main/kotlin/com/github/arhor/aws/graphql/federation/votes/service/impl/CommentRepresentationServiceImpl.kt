package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.model.CommentRepresentation
import com.github.arhor.aws.graphql.federation.votes.data.repository.CommentRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Comment
import com.github.arhor.aws.graphql.federation.votes.service.CommentRepresentationService
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class CommentRepresentationServiceImpl(
    private val commentRepository: CommentRepresentationRepository,
) : CommentRepresentationService {

    override fun findCommentsRepresentationsInBatch(commentIds: Set<UUID>): Map<UUID, Comment> {
        if (commentIds.isEmpty()) {
            return emptyMap()
        }
        val result =
            commentRepository
                .findAllById(commentIds)
                .associateByTo(
                    destination = HashMap(commentIds.size),
                    keySelector = { it.id },
                    valueTransform = { Comment(id = it.id) },
                )

        (commentIds - result.keys).takeIf(Set<UUID>::isNotEmpty)?.let {
            for (commentId in it) {
                result[commentId] = Comment(id = commentId)
            }
            logger.warn("Stubbed comments without representation in the DB: {}", it)
        }
        return result
    }

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

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
