package com.github.arhor.aws.graphql.federation.posts.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table(LikeRef.TABLE_NAME)
@Immutable
data class LikeRef @PersistenceCreator constructor(
    @Column(COL_USER_ID)
    val userId: UUID,

    @CreatedDate
    @Column(COL_CREATED_DATE_TIME)
    val createdDateTime: LocalDateTime? = null,
) {

    // ************************************************************************
    // * Methods `equals` and `hashCode` should be overridden to omit usage   *
    // * of auto-generated field `createdDateTime` in sets and maps.          *
    // ************************************************************************

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LikeRef

        return userId == other.userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }

    companion object {
        const val TABLE_NAME = "posts_have_user_likes"

        // @formatter:off
        const val COL_POST_ID           = "post_id"
        const val COL_USER_ID           = "user_id"
        const val COL_CREATED_DATE_TIME = "created_date_time"
        // @formatter:on

        fun from(user: UserRepresentation) = LikeRef(
            userId = user.id
        )
    }
}
