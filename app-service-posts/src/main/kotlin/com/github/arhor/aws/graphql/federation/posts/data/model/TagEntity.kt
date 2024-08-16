package com.github.arhor.aws.graphql.federation.posts.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(TagEntity.TABLE_NAME)
@Immutable
data class TagEntity @PersistenceCreator constructor(
    @Id
    @Column(COL_ID)
    val id: UUID? = null,

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
