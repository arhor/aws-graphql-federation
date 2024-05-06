package com.github.arhor.aws.graphql.federation.posts.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(TagEntity.TABLE_NAME)
@Immutable
data class TagEntity(
    @Id
    @Column(COL_ID)
    val id: Long? = null,

    @Column(COL_NAME)
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "tags"

        // @formatter:off
        const val COL_ID   = "id"
        const val COL_NAME = "name"
        // @formatter:on
    }
}
