package com.github.arhor.dgs.articles.data.repository

import com.github.arhor.dgs.articles.data.entity.ArticleTagProjection
import com.github.arhor.dgs.articles.data.entity.TagEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository

interface TagRepository : ListCrudRepository<TagEntity, Long> {

    @Query(name = "TagEntity.findAllByArticleIdIn")
    fun findAllByArticleIdIn(articleIds: Collection<Long>): List<ArticleTagProjection>
}
