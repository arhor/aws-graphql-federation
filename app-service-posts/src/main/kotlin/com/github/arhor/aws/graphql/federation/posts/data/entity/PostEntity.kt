package com.github.arhor.aws.graphql.federation.posts.data.entity

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.EnumSet

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

    @Column(COL_CONTENT)
    val content: String,

    @Column("options")
    val options: Options = Options(),

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
    /**
     * Wrapper class over EnumSet is required to make it available for custom conversions.
     */
    data class Options(val items: EnumSet<Option> = EnumSet.noneOf(Option::class.java))

    companion object {
        const val TABLE_NAME = "posts"

        const val COL_ID = "id"
        const val COL_USER_ID = "user_id"
        const val COL_HEADER = "header"
        const val COL_CONTENT = "content"
        const val COL_VERSION = "version"
        const val COL_CREATED_DATE_TIME = "created_date_time"
        const val COL_UPDATED_DATE_TIME = "updated_date_time"
    }
}
