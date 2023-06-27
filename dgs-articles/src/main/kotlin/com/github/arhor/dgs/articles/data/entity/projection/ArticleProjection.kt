package com.github.arhor.dgs.articles.data.entity.projection

data class ArticleProjection(
    val id: Long,
    val userId: Long?,
    val header: String,
    val banner: String?,
    val content: String,
)
