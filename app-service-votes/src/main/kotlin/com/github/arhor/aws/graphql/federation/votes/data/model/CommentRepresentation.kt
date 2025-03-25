package com.github.arhor.aws.graphql.federation.votes.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(CommentRepresentation.TABLE_NAME)
@Immutable
data class CommentRepresentation @PersistenceCreator constructor(
    @Id
    @Column("id")
    val id: UUID,

    @Column("user_id")
    val userId: UUID,

    @Column("post_id")
    val postId: UUID,

    @Transient
    val shouldBePersisted: Boolean = false,
) : Persistable<UUID> {

    override fun getId(): UUID = id

    override fun isNew(): Boolean = shouldBePersisted

    companion object {
        const val TABLE_NAME = "comment_representations"
    }
}
