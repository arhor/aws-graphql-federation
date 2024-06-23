package com.github.arhor.aws.graphql.federation.posts.data.repository.mapping

import com.github.arhor.aws.graphql.federation.posts.data.entity.LikeRef
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity.Companion.COL_CONTENT
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity.Companion.COL_CREATED_DATE_TIME
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity.Companion.COL_ID
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity.Companion.COL_TITLE
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity.Companion.COL_UPDATED_DATE_TIME
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity.Companion.COL_USER_ID
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity.Companion.COL_VERSION
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID
import kotlin.Any
import kotlin.Array
import kotlin.ByteArray
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.String
import kotlin.let
import java.sql.Array as SqlArray

@Component(PostEntityCustomRowMapper.BEAN_NAME)
class PostEntityCustomRowMapper : RowMapper<PostEntity> {

    override fun mapRow(rs: ResultSet, rowNum: Int): PostEntity? =
        PostEntity(
            id = rs.getObject(COL_ID, UUID::class.java),
            userId = rs.getObject(COL_USER_ID, UUID::class.java),
            title = rs.getString(COL_TITLE),
            content = rs.getString(COL_CONTENT),
            version = rs.getLong(COL_VERSION),
            createdDateTime = rs.getObject(COL_CREATED_DATE_TIME, LocalDateTime::class.java),
            updatedDateTime = rs.getObject(COL_UPDATED_DATE_TIME, LocalDateTime::class.java),
            tags = extractUUIDs(rs.getArray(COL_TAGS_IDS), ::TagRef),
            likes = extractUUIDs(rs.getArray(COL_LIKE_IDS), ::LikeRef),
        )

    private inline fun <R> extractUUIDs(data: SqlArray, crossinline ref: (UUID) -> R): Set<R> =
        data.let { it.array as Array<*> }
            .asSequence()
            .filterNotNull()
            .map(::extractUUID)
            .map { ref(it) }
            .toSet()

    private tailrec fun extractUUID(value: Any): UUID =
        when (value) {
            is UUID -> {
                value
            }

            is String -> {
                extractUUID(value = UUID.fromString(value))
            }

            is ByteArray -> {
                extractUUID(value = String(value))
            }

            else -> throw IllegalArgumentException(
                "Cannot extract UUID from the value '$value'of type ${value::class}"
            )
        }

    companion object {
        const val BEAN_NAME = "postEntityCustomRowMapper"

        private const val COL_TAGS_IDS = "tags_ids"
        private const val COL_LIKE_IDS = "like_ids"
    }
}
