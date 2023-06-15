package com.github.arhor.dgs.comments.data.repository

import com.github.arhor.dgs.comments.data.entity.CommentEntity
import org.springframework.data.repository.CrudRepository
import java.util.stream.Stream

interface CommentRepository : CrudRepository<CommentEntity, Long> {

    fun findAllByArticleId(articleId: String): Stream<CommentEntity>

    fun findAllByArticleIdIn(articleIds: Collection<String>): Stream<CommentEntity>

    fun findAllByUserId(userId: String): Stream<CommentEntity>

    fun findAllByUserIdIn(userIds: Collection<String>): Stream<CommentEntity>
}
