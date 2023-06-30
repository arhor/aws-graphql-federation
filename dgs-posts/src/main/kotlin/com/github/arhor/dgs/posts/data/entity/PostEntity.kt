package com.github.arhor.dgs.posts.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(PostEntity.TABLE_NAME)
@Immutable
data class PostEntity(
    @Id
    @Column(COL_ID)
    val id: Long? = null,

    @Column(COL_USER_ID)
    val userId: Long?,

    @Column(COL_HEADER)
    val header: String,

    @Column(COL_BANNER)
    val banner: String?,

    @Column(COL_CONTENT)
    val content: String,

    @Version
    @Column(COL_VERSION)
    val version: Long? = null,

    @CreatedDate
    @Column(COL_CREATED_DATE_TIME)
    val createdDateTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column(COL_UPDATED_DATE_TIME)
    val updatedDateTime: LocalDateTime? = null,

    @MappedCollection(idColumn = TagRef.COL_POST_ID)
    val tags: Set<TagRef> = emptySet()
) {
    companion object {
        const val TABLE_NAME = "posts"

        const val COL_ID = "id"
        const val COL_USER_ID = "user_id"
        const val COL_HEADER = "header"
        const val COL_BANNER = "banner"
        const val COL_CONTENT = "content"
        const val COL_VERSION = "version"
        const val COL_CREATED_DATE_TIME = "created_date_time"
        const val COL_UPDATED_DATE_TIME = "updated_date_time"
    }
}
