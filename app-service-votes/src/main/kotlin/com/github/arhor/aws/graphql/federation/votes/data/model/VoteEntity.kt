package com.github.arhor.aws.graphql.federation.votes.data.model

import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteEntityType
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteValue
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
    val id: UUID? = null,

    @Column("user_id")
    val userId: UUID,

    @Column("entity_id")
    val entityId: UUID,

    @Column("entity_type")
    val entityType: VoteEntityType,

    @Column("value")
    val value: VoteValue,

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
    companion object {
        const val TABLE_NAME = "votes"
    }
}
