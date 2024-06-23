package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID
import java.util.stream.Stream

interface PostRepository : ListCrudRepository<PostEntity, UUID>, PagingAndSortingRepository<PostEntity, UUID> {

    @Query(name = "PostEntity.findAllByUserIdIn")
    fun findAllByUserIdIn(userIds: Collection<UUID>): List<PostEntity>

    @Query(name = "PostEntity.findPageByTagsContaining")
    fun findPageByTagsContaining(tags: Collection<String>, limit: Int, offset: Long): Stream<PostEntity>

    @Query(name = "PostEntity.countByTagsContaining")
    fun countByTagsContaining(tags: Collection<String>): Long
}
