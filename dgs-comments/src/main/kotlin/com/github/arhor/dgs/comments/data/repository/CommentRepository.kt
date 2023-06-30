package com.github.arhor.dgs.comments.data.repository

import com.github.arhor.dgs.comments.data.entity.CommentEntity
import org.springframework.data.repository.CrudRepository
import java.util.stream.Stream

interface CommentRepository : CrudRepository<CommentEntity, Long> {
    fun findAllByUserId(userId: Long): Stream<CommentEntity>
    fun findAllByUserIdIn(userIds: Collection<Long>): Stream<CommentEntity>
    fun findAllByPostId(userId: Long): Stream<CommentEntity>
    fun findAllByPostIdIn(postIds: Collection<Long>): Stream<CommentEntity>
}
