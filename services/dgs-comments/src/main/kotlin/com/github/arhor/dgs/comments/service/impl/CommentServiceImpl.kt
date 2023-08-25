package com.github.arhor.dgs.comments.service.impl

import com.github.arhor.dgs.comments.data.entity.CommentEntity
import com.github.arhor.dgs.comments.data.repository.CommentRepository
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.COMMENT
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentInput
import com.github.arhor.dgs.comments.service.CommentService
import com.github.arhor.dgs.comments.service.mapper.CommentMapper
import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import com.github.arhor.dgs.lib.exception.Operation
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors.groupingBy
import java.util.stream.Stream

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val commentMapper: CommentMapper,
) : CommentService {

    @Transactional(readOnly = true)
    override fun getCommentsByUserIds(userIds: Collection<Long>): Map<Long, List<Comment>> {
        return findCommentsThenGroupBy(
            ids = userIds,
            dataSource = commentRepository::findAllByUserIdIn,
            classifier = { it.userId!! },
        )
    }

    @Transactional(readOnly = true)
    override fun getCommentsByPostIds(postIds: Collection<Long>): Map<Long, List<Comment>> {
        return findCommentsThenGroupBy(
            ids = postIds,
            dataSource = commentRepository::findAllByPostIdIn,
            classifier = { it.postId },
        )
    }

    @Transactional
    override fun createComment(input: CreateCommentInput): Comment {
        return commentMapper.mapToEntity(input)
            .let { commentRepository.save(it) }
            .let { commentMapper.mapToDTO(it) }
    }

    @Transactional
    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    override fun updateComment(input: UpdateCommentInput): Comment {
        val initialState = commentRepository.findByIdOrNull(input.id) ?: throw EntityNotFoundException(
            entity = COMMENT.TYPE_NAME,
            condition = "${COMMENT.Id} = ${input.id}",
            operation = Operation.UPDATE,
        )
        var currentState = initialState

        input.content?.let {
            currentState = currentState.copy(content = it)
        }

        return commentMapper.mapToDTO(
            entity = when (currentState != initialState) {
                true -> commentRepository.save(currentState)
                else -> initialState
            }
        )
    }

    @Transactional
    override fun deleteComment(id: Long): Boolean {
        return when (val comment = commentRepository.findByIdOrNull(id)) {
            null -> false
            else -> {
                commentRepository.delete(comment)
                true
            }
        }
    }

    @Transactional
    override fun unlinkCommentsFromUser(userId: Long) {
        commentRepository.unlinkAllFromUser(userId)
    }

    @Transactional
    override fun deleteCommentsFromPost(postId: Long) {
        commentRepository.deleteAllFromPost(postId)
    }

    /**
     * @param ids     ids of the comments
     * @param dataSource function that will be used to load comments in case ids collection is not empty
     * @param classifier function that will be used to classify object for grouping operation
     */
    private inline fun <K> findCommentsThenGroupBy(
        ids: Collection<K>,
        dataSource: (Collection<K>) -> Stream<CommentEntity>,
        noinline classifier: (Comment) -> K,
    ): Map<K, List<Comment>> {
        return when {
            ids.isNotEmpty() -> {
                dataSource(ids).use { data ->
                    data.map(commentMapper::mapToDTO)
                        .collect(groupingBy(classifier))
                }
            }

            else -> {
                emptyMap()
            }
        }
    }
}
