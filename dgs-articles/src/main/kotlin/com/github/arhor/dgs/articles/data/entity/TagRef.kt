package com.github.arhor.dgs.articles.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Table

@Table(TagRef.TABLE_NAME)
@Immutable
data class TagRef(
    @Id
    val id: Long? = null,
    val tagId: AggregateReference<TagEntity, Long>,
) {
    companion object {
        const val TABLE_NAME = "articles_has_tags"
    }
}
