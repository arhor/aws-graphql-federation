package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.model.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.mapping.PostIdToTagNamesResultSetExtractor
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository
import java.util.UUID

interface TagRepository : ListCrudRepository<TagEntity, UUID> {

    @Query(
        name = "TagEntity.findAllByPostIdIn",
        resultSetExtractorRef = PostIdToTagNamesResultSetExtractor.BEAN_NAME,
    )
    fun findAllByPostIdIn(postIds: Collection<UUID>): Map<UUID, List<String>>

    fun findAllByNameIn(tagNames: Collection<String>): List<TagEntity>
}
