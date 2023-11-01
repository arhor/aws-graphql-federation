package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.mapping.PostIdToTagNamesResultSetExtractor
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository

interface TagRepository : ListCrudRepository<TagEntity, Long> {

    @Query(
        name = "TagEntity.findAllByIdIn",
        resultSetExtractorRef = PostIdToTagNamesResultSetExtractor.BEAN_NAME,
    )
    fun findAllByArticleIdIn(postIds: Collection<Long>): Map<Long, List<String>>

    fun findAllByNameIn(tagNames: Collection<String>): List<TagEntity>
}
