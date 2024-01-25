package com.github.arhor.aws.graphql.federation.users.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table(OutboxEventEntity.TABLE_NAME)
@Immutable
data class OutboxEventEntity(
    @Id
    @Column("id")
    val id: UUID? = null,

    @Column("type")
    val type: String,

    @Column("payload")
    val payload: Map<String, Any?>,

    @Column("headers")
    val headers: Map<String, Any?>,

    @CreatedDate
    @Column("created_date_time")
    val createdDateTime: LocalDateTime? = null,
) {
    companion object {
        const val TABLE_NAME = "outbox_events"
    }
}
