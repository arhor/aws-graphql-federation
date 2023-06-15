package com.github.arhor.dgs.articles.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("articles")
@Immutable
data class ArticleEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("user_id")
    val userId: Long,

    @Column("name")
    val name: String,

    @Column("content")
    val content: String
)
