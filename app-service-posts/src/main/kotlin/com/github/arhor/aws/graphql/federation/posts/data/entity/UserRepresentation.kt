package com.github.arhor.aws.graphql.federation.posts.data.entity

import com.github.arhor.aws.graphql.federation.spring.core.data.Features
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.EnumSet
import java.util.UUID

@Table(UserRepresentation.TABLE_NAME)
@Immutable
data class UserRepresentation @PersistenceCreator constructor(
    @Id
    @Column("id")
    private val id: UUID,

    @Column("features")
    val features: Features<Feature> = Features(items = EnumSet.noneOf(Feature::class.java)),

    @Transient
    val shouldBePersisted: Boolean = false,
) : Persistable<UUID> {

    override fun getId(): UUID = id

    override fun isNew(): Boolean = shouldBePersisted

    enum class Feature {
        POSTS_DISABLED,
    }

    companion object {
        const val TABLE_NAME = "user_representations"
    }
}
