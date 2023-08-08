package com.github.arhor.dgs.comments.data.repository

import com.github.arhor.dgs.comments.data.entity.CommentEntity
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.stream.Stream

interface CommentRepository : CrudRepository<CommentEntity, Long> {
    fun findAllByUserIdIn(userIds: Collection<Long>): Stream<CommentEntity>
    fun findAllByPostIdIn(postIds: Collection<Long>): Stream<CommentEntity>

    @Modifying
    @Query(name = "CommentEntity.unlinkAllFromUser")
    fun unlinkAllFromUser(userId: Long)

    @Modifying
    @Query(name = "CommentEntity.deleteAllFromPost")
    fun deleteAllFromPost(postId: Long)
}
