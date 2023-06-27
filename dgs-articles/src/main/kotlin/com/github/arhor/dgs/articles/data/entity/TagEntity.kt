package com.github.arhor.dgs.articles.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(TagEntity.TABLE_NAME)
@Immutable
data class TagEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("name")
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "tags"

        fun new(name: String) = TagEntity(name = name)
    }
}
