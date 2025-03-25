package com.github.arhor.aws.graphql.federation.votes.data.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table(VoteEntity.TABLE_NAME)
@Immutable
data class VoteEntity(
    @Id
    var id: UUID? = null,

    @Column("entity_id")
    var entityId: UUID,

    @Column("entity_type")
    var entityType: EntityType,

    @Column("value")
    var value: Int,

    @Version
    @Column("version")
    val version: Long? = null,

    @CreatedDate
    @Column("created_date_time")
    val createdDateTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column("updated_date_time")
    val updatedDateTime: LocalDateTime? = null,
) {
    enum class EntityType {
        POST,
        COMMENT,
    }

    companion object {
        const val TABLE_NAME = "votes"
    }
}
