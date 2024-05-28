package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID
import java.util.stream.Stream

interface PostRepository : ListCrudRepository<PostEntity, UUID>, PagingAndSortingRepository<PostEntity, UUID> {

    @Query(name = "PostProjection.findAllByUserIdIn")
    fun findAllByUserIdIn(userIds: Collection<UUID>): List<PostProjection>

    @Query(name = "PostProjection.findPageByTagsContaining")
    fun findPageByTagsContaining(tags: Collection<String>, limit: Int, offset: Long): Stream<PostProjection>

    @Query(name = "PostProjection.countByTagsContaining")
    fun countByTagsContaining(tags: Collection<String>): Long
}
