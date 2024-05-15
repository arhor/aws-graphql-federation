package com.github.arhor.aws.graphql.federation.posts.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(UserRepresentationEntity.TABLE_NAME)
@Immutable
data class UserRepresentationEntity(
    @Id
    @Column("id")
    private val id: UUID,
) : Persistable<UUID> {

    override fun getId(): UUID = id

    override fun isNew(): Boolean = true

    companion object {
        const val TABLE_NAME = "user_representations"
    }
}
