package com.github.arhor.dgs.articles.data.entity

import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(TagEntity.TABLE_NAME)
@Immutable
@QueryEntity
data class TagEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("name")
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "tags"
    }
}
