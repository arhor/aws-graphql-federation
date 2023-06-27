package com.github.arhor.dgs.articles.data.repository

import com.github.arhor.dgs.articles.data.entity.TagEntity
import com.github.arhor.dgs.articles.data.repository.mapping.ArticleIdToTagNamesResultSetExtractor
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository

interface TagRepository : ListCrudRepository<TagEntity, Long> {

    @Query(
        name = "TagEntity.findAllByArticleIdIn",
        resultSetExtractorRef = ArticleIdToTagNamesResultSetExtractor.BEAN_NAME,
    )
    fun findAllByArticleIdIn(articleIds: Collection<Long>): Map<Long, List<String>>

    fun findAllByNameIn(tagNames: Collection<String>): List<TagEntity>
}
