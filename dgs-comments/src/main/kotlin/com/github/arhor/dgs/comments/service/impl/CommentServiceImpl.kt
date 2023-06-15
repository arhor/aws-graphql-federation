package com.github.arhor.dgs.comments.service.impl

import com.github.arhor.dgs.comments.data.entity.CommentEntity
import com.github.arhor.dgs.comments.data.repository.CommentRepository
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentRequest
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentRequest
import com.github.arhor.dgs.comments.service.CommentService
import com.github.arhor.dgs.comments.service.mapper.CommentMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors.groupingBy
import java.util.stream.Stream

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val commentMapper: CommentMapper,
) : CommentService {

    @Transactional
    override fun createComment(request: CreateCommentRequest): Comment {
        return commentMapper.mapToEntity(request)
            .let { commentRepository.save(it) }
            .let { commentMapper.mapToDTO(it) }
    }

    override fun updateComment(request: UpdateCommentRequest): Comment {
        TODO("Not yet implemented")
    }

    override fun deleteComment(id: Long) {
        TODO("Not yet implemented")
    }

    @Transactional(readOnly = true)
    override fun getCommentsUserId(userId: String): List<Comment> {
        return findInternal(
            id = userId,
            source = commentRepository::findAllByUserId
        )
    }

    @Transactional(readOnly = true)
    override fun getCommentsByUserIds(userIds: Collection<String>): Map<String, List<Comment>> {
        return findInternalInBatch(
            ids = userIds,
            source = commentRepository::findAllByUserIdIn,
            classifier = Comment::userId
        )
    }

    @Transactional(readOnly = true)
    override fun getCommentsByTopicId(articleId: String): List<Comment> {
        return findInternal(
            id = articleId,
            source = commentRepository::findAllByArticleId
        )
    }

    @Transactional(readOnly = true)
    override fun getCommentsByTopicIds(articleIds: Collection<String>): Map<String, List<Comment>> {
        return findInternalInBatch(
            ids = articleIds,
            source = commentRepository::findAllByArticleIdIn,
            classifier = Comment::articleId
        )
    }

    private inline fun findInternal(id: String, source: (String) -> Stream<CommentEntity>): List<Comment> {
        return source.invoke(id).use { data ->
            data.map(commentMapper::mapToDTO)
                .toList()
        }
    }

    private inline fun findInternalInBatch(
        ids: Collection<String>,
        source: (Collection<String>) -> Stream<CommentEntity>,
        crossinline classifier: (Comment) -> String
    ): Map<String, List<Comment>> {

        return when {
            ids.isNotEmpty() -> {
                source.invoke(ids).use { data ->
                    data.map(commentMapper::mapToDTO)
                        .groupBy(classifier)
                }
            }

            else -> {
                emptyMap()
            }
        }
    }

    private inline fun <T, K> Stream<T>.groupBy(crossinline classifier: (T) -> K): Map<K, List<T>> {
        return collect(groupingBy { classifier.invoke(it) })
    }
}
