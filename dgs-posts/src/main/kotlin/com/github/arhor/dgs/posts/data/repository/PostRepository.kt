package com.github.arhor.dgs.posts.data.repository

import com.github.arhor.dgs.posts.data.entity.PostEntity
import com.github.arhor.dgs.posts.data.entity.projection.PostProjection
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository
import java.util.stream.Stream

interface PostRepository : ListCrudRepository<PostEntity, Long> {

    @Query(name = "PostProjection.findAll")
    fun findAll(limit: Long, offset: Long): List<PostProjection>

    fun findAllByUserId(userId: Long): Stream<PostEntity>

    @Query(name = "PostProjection.findAllByUserIdIn")
    fun findAllByUserIdIn(userIds: Collection<Long>): List<PostProjection>
}
