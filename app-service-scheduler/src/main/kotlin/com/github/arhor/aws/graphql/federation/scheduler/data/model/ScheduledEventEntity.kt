package com.github.arhor.aws.graphql.federation.scheduler.data.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table(ScheduledEventEntity.TABLE_NAME)
@Immutable
data class ScheduledEventEntity @PersistenceCreator constructor(
    @Id
    @Column("id")
    private val id: UUID,

    @Column("type")
    val type: String,

    @Column("data")
    val data: String,

    @Column("publish_date_time")
    val publishDateTime: LocalDateTime,

    @CreatedDate
    @Column("created_date_time")
    val createdDateTime: LocalDateTime? = null,

    @Transient
    val shouldBePersisted: Boolean = false,
) : Persistable<UUID> {

    override fun getId(): UUID = id

    override fun isNew(): Boolean = shouldBePersisted

    companion object {
        const val TABLE_NAME = "scheduled_events"
    }
}
