package com.github.arhor.dgs.extradata.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("extra_data")
@Immutable
data class ExtraDataEntity(
    @Id
    @Column("")
    val id: UUID? = null,

    @Column("entity_id")
    val entityId: String,

    @Column("entity_type")
    val entityType: String,

    @Column("data")
    val data: Map<String, Any>,
)
