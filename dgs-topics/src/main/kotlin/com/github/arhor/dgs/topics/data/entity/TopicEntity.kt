package com.github.arhor.dgs.topics.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("topics")
@Immutable
data class TopicEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("name")
    val name: String,

    @Column("user_id")
    val userId: Long?,
)
