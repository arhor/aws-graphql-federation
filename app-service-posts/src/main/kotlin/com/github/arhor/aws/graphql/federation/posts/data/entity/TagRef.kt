package com.github.arhor.aws.graphql.federation.posts.data.entity

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
        const val TABLE_NAME = "posts_has_tags"

        const val COL_ID = "id"
        const val COL_POST_ID = "post_id"
        const val COL_TAG_ID = "tag_id"

        fun create(entity: TagEntity) = TagRef(
            tagId = AggregateReference.to(
                entity.id ?: throw IllegalStateException("Referenced entity must be persisted")
            )
        )
    }
}
