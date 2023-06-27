package com.github.arhor.dgs.articles.data.repository

import com.github.arhor.dgs.articles.data.entity.ArticleEntity
import com.github.arhor.dgs.articles.data.entity.projection.ArticleProjection
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository

interface ArticleRepository : ListCrudRepository<ArticleEntity, Long> {

    @Query(name = "ArticleProjection.findAll")
    fun findAll(limit: Long, offset: Long): List<ArticleProjection>

    @Query(name = "ArticleProjection.findAllByUserIdIn")
    fun findAllByUserIdIn(userIds: Collection<Long>): List<ArticleProjection>
}
