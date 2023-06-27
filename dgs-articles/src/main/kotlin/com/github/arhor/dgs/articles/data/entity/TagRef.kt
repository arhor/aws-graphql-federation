package com.github.arhor.dgs.articles.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(TagRef.TABLE_NAME)
@Immutable
data class TagRef(
    @Id
    @Column(COL_ID)
    val id: Long? = null,

    @Column(COL_TAG_ID)
    val tagId: AggregateReference<TagEntity, Long>,
) {
    companion object {
        const val TABLE_NAME = "articles_has_tags"

        const val COL_ID = "id"
        const val COL_ARTICLE_ID = "article_id"
        const val COL_TAG_ID = "tag_id"

        fun create(entity: TagEntity): TagRef = TagRef(
            tagId = AggregateReference.to(
                entity.id ?: throw IllegalStateException("${TagEntity::class.simpleName} must be persisted")
            )
        )
    }
}
