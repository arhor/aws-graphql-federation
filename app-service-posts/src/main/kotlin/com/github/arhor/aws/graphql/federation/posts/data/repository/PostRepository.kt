package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository
import java.util.UUID

interface PostRepository : ListCrudRepository<PostEntity, UUID> {

    @Query(name = "PostProjection.findAll")
    fun findAll(limit: Int, offset: Int): List<PostProjection>

    @Query(name = "PostProjection.findAllByUserIdIn")
    fun findAllByUserIdIn(userIds: Collection<UUID>): List<PostProjection>
}
