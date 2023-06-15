package com.github.arhor.dgs.comments.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("comments")
@Immutable
data class CommentEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("user_id")
    val userId: Long?,

    @Column("article_id")
    val articleId: Long,

    @Column("content")
    val content: String,

    @Version
    @Column("version")
    val version: Long? = null,

    @CreatedDate
    @Column("created_date_time")
    val createdDateTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column("updated_date_time")
    val updatedDateTime: LocalDateTime? = null,
)
